package net.aircommunity.platform.service.internal;

import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.FileUploadResult;
import net.aircommunity.platform.service.FileUploadService;

/**
 * FileUpload Service implementation based on Qiniu.
 *
 * @author Bin.Zhang
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {
	private static final Logger LOG = LoggerFactory.getLogger(FileUploadServiceImpl.class);

	private static final String MIME = "application/octet-stream";
	private static final String FILE_URL_FORMAT = "%s/%s"; // host/file_name

	private UploadManager uploadManager;
	private String uploadToken;

	@Resource
	private Configuration configuration;

	@PostConstruct
	private void init() {
		com.qiniu.storage.Configuration config = new com.qiniu.storage.Configuration(Zone.autoZone());
		uploadManager = new UploadManager(config);
		Auth auth = Auth.create(configuration.getFileUploadAccessKey(), configuration.getFileUploadSecretKey());
		uploadToken = auth.uploadToken(configuration.getFileUploadBucket());
	}

	@Override
	public FileUploadResult upload(String fileName, InputStream stream) {
		try {
			Response response = uploadManager.put(stream, fileName, uploadToken, null, MIME);
			if (response.isOK()) {
				return FileUploadResult
						.success(String.format(FILE_URL_FORMAT, configuration.getFileUploadHost(), fileName));
			}
			return FileUploadResult.failure(response.error);
		}
		catch (Exception e) {
			LOG.error(String.format("Got error when upload file: %s, cause: %s", fileName, e.getMessage()), e);
			if (QiniuException.class.isAssignableFrom(e.getClass())) {
				return FileUploadResult.failure(((QiniuException) e).response.error);
			}
			return FileUploadResult.failure(e.getMessage());
		}
	}

}

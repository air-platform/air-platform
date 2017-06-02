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
import com.qiniu.processing.OperationManager;
import com.qiniu.processing.OperationStatus;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.FileUploadResult;
import net.aircommunity.platform.model.ImageCropResult;
import net.aircommunity.platform.service.FileService;

/**
 * FileUpload Service implementation based on Qiniu.
 *
 * @author Bin.Zhang
 */
@Service
public class FileServiceImpl implements FileService {
	private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

	private static final String MIME = "application/octet-stream";
	// host/file_name
	private static final String FILE_URL_FORMAT = "%s/%s";
	// host/thumbnail_file_name
	private static final String AVATAR_FILE_NAME_FORMAT = "thumbnail_%s";
	// e.g. imageMogr2/crop/!300x300a50a50/quality/75|saveas/xxxbase64xxxx
	private static final String FOPS_FORMAT = "imageMogr2/crop/!%s/quality/75|saveas/%s";
	// 0 成功，1 等待处理，2 正在处理，3 处理失败，4 通知提交失败
	private static final int OP_SUCCESS = 0;
	private static final int OP_PENDING = 1;
	private static final int OP_PROCESSING = 2;
	private static final int TOKEN_EXPIRES = 5 * 60; // 5 minutes

	private Auth auth;
	private UploadManager uploadManager;
	private OperationManager operationManager;

	@Resource
	private Configuration configuration;

	@PostConstruct
	private void init() {
		com.qiniu.storage.Configuration config = new com.qiniu.storage.Configuration(Zone.autoZone());
		uploadManager = new UploadManager(config);
		auth = Auth.create(configuration.getFileUploadAccessKey(), configuration.getFileUploadSecretKey());
		operationManager = new OperationManager(auth, config);
	}

	@Override
	public FileUploadResult upload(String fileName, InputStream stream) {
		try {
			String uploadToken = auth.uploadToken(configuration.getFileUploadBucket(), null, TOKEN_EXPIRES, null, true);
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

	@Override
	public ImageCropResult cropImage(String imageFilename, InputStream stream, String cropOptions) {
		FileUploadResult result = upload(imageFilename, stream);
		if (!result.isSuccess()) {
			return ImageCropResult.failure(result.getMessage());
		}
		String thumbnailImage = String.format(AVATAR_FILE_NAME_FORMAT, imageFilename);
		String urlbase64 = UrlSafeBase64
				.encodeToString(String.format("%s:%s", configuration.getFileUploadBucket(), thumbnailImage));
		String pfops = String.format(FOPS_FORMAT, cropOptions, urlbase64);
		try {
			String opId = operationManager.pfop(configuration.getFileUploadBucket(), imageFilename, pfops,
					new StringMap().putWhen("force", 1, true));
			OperationStatus status = operationManager.prefop(opId);
			if (status.code == OP_SUCCESS || status.code == OP_PENDING || status.code == OP_PROCESSING) {
				return ImageCropResult
						.success(String.format(FILE_URL_FORMAT, configuration.getFileUploadHost(), thumbnailImage));
			}
			return ImageCropResult.failure(status.desc);
		}
		catch (Exception e) {
			LOG.error(String.format("Got error when crop file: %s, cause: %s", imageFilename, e.getMessage()), e);
			if (QiniuException.class.isAssignableFrom(e.getClass())) {
				return ImageCropResult.failure(((QiniuException) e).response.error);
			}
			return ImageCropResult.failure(e.getMessage());
		}
	}

}

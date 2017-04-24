package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micro.annotation.RESTful;
import io.micro.annotation.multipart.MultipartForm;
import io.micro.common.UUIDs;
import io.micro.common.io.MoreFiles;
import io.swagger.annotations.Api;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.FileUploadResult;
import net.aircommunity.platform.model.StreamingImageFile;
import net.aircommunity.platform.service.FileUploadService;

/**
 * Files RESTful API.
 * 
 * @author Bin.Zhang
 */
@Api
@RESTful
@Path("files")
public class FileResource {
	private static final Logger LOG = LoggerFactory.getLogger(FileResource.class);

	// randomString.extension, e.g. jki45hkfh945k3j5.jpg
	private static final String FILE_NAME_FORMAT = "%s.%s";

	@Resource
	private Configuration configuration;

	@Resource
	private FileUploadService fileUploadService;

	/**
	 * File upload to cloud
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	// @Authenticated
	// TODO enable Authenticated, once markdown editor can support auth
	public Response uploadFileToCloud(@MultipartForm StreamingImageFile inputFile, @Context SecurityContext context) {
		try {
			LOG.debug("Uploading file {} to cloud", inputFile.getFileName());
			String extension = MoreFiles.getExtension(inputFile.getFileName());
			FileUploadResult result = fileUploadService
					.upload(String.format(FILE_NAME_FORMAT, UUIDs.shortRandom(), extension), inputFile.getFileData());
			return Response.ok(result).build();
		}
		finally {
			if (inputFile != null) {
				inputFile.close();
			}
		}
	}
}

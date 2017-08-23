package net.aircommunity.platform.rest;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.FileUploadResult;
import net.aircommunity.platform.model.StreamingImageFile;
import net.aircommunity.platform.model.Ueditor;
import net.aircommunity.platform.model.UeditorConfig;
import net.aircommunity.platform.model.UeditorUploadResult;
import net.aircommunity.platform.service.common.FileService;

/**
 * Files RESTful API.
 * 
 * @author Xiangwen.Kong
 */
@Api
@RESTful
@Path("ueditor")
public class UeditorResource {
	private static final Logger LOG = LoggerFactory.getLogger(UeditorResource.class);

	@Resource
	private FileService fileService;

	@Resource
	private UeditorConfig ueditorConfig;

	/**
	 * Ueditor configuration
	 */
	@GET
	@Path("upload")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response ueditorUploadConfig(@NotNull @QueryParam("action") Ueditor.ActionType actionType,
			@QueryParam("page") @DefaultValue("0") int page, @QueryParam("pageSize") @DefaultValue("0") int pageSize) {
		LOG.debug("Get ueditor config, {}", actionType);
		switch (actionType) {
		case CONFIG:
			return Response.ok(ueditorConfig).build();
		default:
		}
		return Response.ok(new UeditorConfig()).build();
	}

	/**
	 * Ueditor configuration
	 */
	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public UeditorUploadResult ueditorUploadImage(@NotNull @QueryParam("action") Ueditor.ActionType actionType,
			@MultipartForm StreamingImageFile inputFile, @Context SecurityContext context) {
		LOG.debug("Get ueditor config, {}", actionType);
		switch (actionType) {
		case UPLOADIMAGE:
			try {
				LOG.debug("Uploading file {} to cloud", inputFile.getFileName());
				String extension = MoreFiles.getExtension(inputFile.getFileName());

				FileUploadResult fur = fileService.upload(
						String.format(Constants.FILE_UPLOAD_NAME_FORMAT, UUIDs.shortRandom(), extension),
						inputFile.getFileData());

				if (fur.isSuccess()) {
					return UeditorUploadResult.success(inputFile.getFileName(), fur.getUrl(), inputFile.getFileName());
				}
				else {
					return UeditorUploadResult.failure(inputFile.getFileName(), inputFile.getFileName());
				}

			}
			finally {
				if (inputFile != null) {
					inputFile.close();
				}
			}
		default:
		}
		return UeditorUploadResult.failure(inputFile.getFileName(), inputFile.getFileName());
	}

}

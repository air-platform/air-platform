package net.aircommunity.platform.rest;

import io.micro.annotation.RESTful;
import io.micro.annotation.multipart.MultipartForm;
import io.micro.common.UUIDs;
import io.micro.common.io.MoreFiles;
import io.swagger.annotations.Api;
import net.aircommunity.platform.Constants;
import net.aircommunity.platform.model.*;
import net.aircommunity.platform.service.common.FileService;
import net.aircommunity.platform.service.common.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Files RESTful API.
 * 
 * @author Xiangwen.Kong
 */
@Api
@RESTful
@Path("notification")
public class NotificationResource {
	private static final Logger LOG = LoggerFactory.getLogger(NotificationResource.class);


	@Resource
	private NotificationService notificationService;

	/**
	 * Ueditor configuration
	 */
	@POST
	@Path("send")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ Roles.ROLE_ADMIN})
	public Response sendNotification(@NotNull @QueryParam("message") String message) {
		LOG.debug("Get notification {}", message);
		notificationService.sendNotification(message);
		return Response.noContent().build();
	}


	/**
	 * Ueditor configuration

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public UeditorUploadResult ueditorUploadImage(@NotNull @QueryParam("action") Ueditor.ActionType actionType,
									   @MultipartForm StreamingImageFile inputFile,
									   @Context SecurityContext context) {
		LOG.debug("Get ueditor config, {}", actionType);
		switch (actionType) {
			case UPLOADIMAGE:
				try {
					LOG.debug("Uploading file {} to cloud", inputFile.getFileName());
					String extension = MoreFiles.getExtension(inputFile.getFileName());

					FileUploadResult fur =  fileService.upload(String.format(Constants.FILE_UPLOAD_NAME_FORMAT, UUIDs.shortRandom(), extension),
							inputFile.getFileData());

					if(fur.isSuccess()){
						return UeditorUploadResult.success(inputFile.getFileName(), fur.getUrl(), inputFile.getFileName());
					}else{
						return UeditorUploadResult.failure(inputFile.getFileName(),inputFile.getFileName());
					}

				}
				finally {
					if (inputFile != null) {
						inputFile.close();
					}
				}
			default:
		}
		return UeditorUploadResult.failure(inputFile.getFileName(),inputFile.getFileName());
	}*/







}

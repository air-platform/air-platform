package net.aircommunity.platform.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;

import javax.annotation.Resource;
import javax.json.Json;
import javax.ws.rs.Consumes;
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

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.common.io.MoreFiles;
import net.aircommunity.rest.annotation.Authenticated;
import net.aircommunity.rest.annotation.RESTful;
import net.aircommunity.rest.annotation.multipart.MultipartForm;
import net.aircommunity.rest.support.StreamingInputFile;

/**
 * Files RESTful API.
 * 
 * @author Bin.Zhang
 */
@RESTful
@Path("files")
public class FileResource {
	private static final Logger LOG = LoggerFactory.getLogger(FileResource.class);

	private static final String FILE_JSON_PROP = "file";

	@Resource
	private Configuration configuration;

	/**
	 * File upload to <b>uploadDirRoot/accountId/xxx_file_name</b>
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response uploadFile(@QueryParam("replace") boolean replaceExisting,
			@MultipartForm StreamingInputFile inputFile, @Context SecurityContext context) {
		String accountId = context.getUserPrincipal().getName();
		java.nio.file.Path filePath = copyFile(inputFile, Paths.get(configuration.getFileUploadDir(), accountId));
		LOG.debug("File {} uploaded to {}", inputFile.getFileName(), filePath);
		return Response.ok(Json.createObjectBuilder().add(FILE_JSON_PROP, filePath.toString()).build()).build();
	}

	private static java.nio.file.Path copyFile(StreamingInputFile inputFile, java.nio.file.Path fileUploadDir) {
		try {
			File dir = fileUploadDir.toFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			java.nio.file.Path metadataFilePath = Paths.get(dir.getAbsolutePath(), inputFile.getFileName());
			try (BufferedOutputStream output = new BufferedOutputStream(
					new FileOutputStream(metadataFilePath.toFile()))) {
				MoreFiles.copy(inputFile.getFileData(), output);
			}
			return metadataFilePath;
		}
		catch (Exception e) {
			throw new AirException(Codes.INTERNAL_ERROR,
					String.format("Internal error when copy metadata cause: %s", e.getMessage()), e);
		}
		finally {
			if (inputFile != null) {
				inputFile.close();
			}
		}
	}
}

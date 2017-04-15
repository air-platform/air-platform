package net.aircommunity.platform.service;

import java.io.InputStream;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.FileUploadResult;

/**
 * FileUpload Service
 * 
 * @author Bin.Zhang
 */
public interface FileUploadService {

	/**
	 * Upload file.
	 * 
	 * @param fileName the file name
	 * @param stream file inputStream
	 * @return result
	 */
	@Nonnull
	FileUploadResult upload(@Nonnull String fileName, @Nonnull InputStream stream);

}

package net.aircommunity.platform.service.common;

import java.io.InputStream;

import javax.annotation.Nonnull;

import net.aircommunity.platform.model.FileUploadResult;
import net.aircommunity.platform.model.ImageCropResult;

/**
 * File Service (upload, image crop, etc.)
 * 
 * @author Bin.Zhang
 */
public interface FileService {

	/**
	 * Upload file.
	 * 
	 * @param fileName the file name
	 * @param stream file inputStream
	 * @return result
	 */
	@Nonnull
	FileUploadResult upload(@Nonnull String fileName, @Nonnull InputStream stream);

	/**
	 * Upload and crop image file.
	 * 
	 * @param imageFilename the image file name
	 * @param stream file inputStream
	 * @param cropOptions image cropOptions, ignored if null
	 * @return result
	 */
	@Nonnull
	ImageCropResult cropImage(@Nonnull String imageFilename, @Nonnull InputStream stream, @Nonnull String cropOptions);

}

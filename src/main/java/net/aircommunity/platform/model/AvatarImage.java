package net.aircommunity.platform.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.multipart.MultipartFilename;
import io.micro.annotation.multipart.MultipartType;

/**
 * Streaming input file model, must have input name <tt>file</tt>
 * 
 * <pre>
 * <input type="file" name="file" />
 * </pre>
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class AvatarImage implements Serializable {
	private static final long serialVersionUID = 1L;

	@MultipartFilename("file")
	private String fileName;

	@FormParam("file")
	@MultipartType("application/octet-stream")
	// OR byte[] fileData;
	private InputStream fileData;

	public String getFileName() {
		return fileName;
	}

	public InputStream getFileData() {
		return fileData;
	}

	public void close() {
		if (fileData != null) {
			try {
				fileData.close();
			}
			catch (IOException e) {
				// ignore
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AvatarImage [fileName=").append(fileName).append("]");
		return builder.toString();
	}
}

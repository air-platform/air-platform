package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * FileUpload Result (not well modeled, just required by a Markdown editor)
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class FileUploadResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int SUCCESS = 1;
	private static final int FAILURE = 0;

	// the field name is required by MD editor

	// 0:failure, 1: success
	private int success;

	// result message (success or failure)
	private String message;

	// uploaded url
	private String url;

	public static FileUploadResult success(String url) {
		return new FileUploadResult(SUCCESS, "success", url);
	}

	public static FileUploadResult failure(String message) {
		return new FileUploadResult(FAILURE, message, null);
	}

	private FileUploadResult(int success, String message, String url) {
		this.success = success;
		this.message = message;
		this.url = url;
	}

	public int getSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public String getUrl() {
		return url;
	}

	public boolean isSuccess() {
		return success == SUCCESS;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FileUploadResult [success=").append(success).append(", message=").append(message)
				.append(", url=").append(url).append("]");
		return builder.toString();
	}

}

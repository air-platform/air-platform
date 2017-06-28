package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * ImageCrop Result
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class ImageCropResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int SUCCESS = 0;
	private static final int FAILURE = 1;

	// 0:failure, 1: success
	private int result;

	// result message (success or failure)
	private String message;

	// uploaded url
	private String url;

	public static ImageCropResult success(String url) {
		return new ImageCropResult(SUCCESS, "success", url);
	}

	public static ImageCropResult failure(String message) {
		return new ImageCropResult(FAILURE, message, null);
	}

	private ImageCropResult(int result, String message, String url) {
		this.result = result;
		this.message = message;
		this.url = url;
	}

	public int getResult() {
		return result;
	}

	public String getMessage() {
		return message;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImageCropResult [result=").append(result).append(", message=").append(message).append(", url=")
				.append(url).append("]");
		return builder.toString();
	}

}

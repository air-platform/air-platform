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
public class UeditorUploadResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private String state;
	private String url;
	private String title;
	private String original;

	public static UeditorUploadResult success(String title, String url, String original) {
		return new UeditorUploadResult("SUCCESS", title, url, original);
	}

	public static UeditorUploadResult failure(String title, String original) {
		return new UeditorUploadResult("FAILURE", title, "", original);
	}

	private UeditorUploadResult(String state, String title, String url, String original) {
		this.state = state;
		this.title = title;
		this.url = url;
		this.original = original;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UeditorUploadResult [state=").append(state).append(", url=").append(url).append(", title=")
				.append(title).append(", original=").append(original).append("]");
		return builder.toString();
	}
}

package net.aircommunity.platform.model;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

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
		builder.append("FileUploadResult [success=").append(state).append(", title=").append(title)
				.append(", url=").append(url).append("]");
		return builder.toString();
	}

}

package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * AirQ Topic
 * 
 * @author luocheng
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AirqTopic implements Serializable {
	private static final long serialVersionUID = 1L;

	private String category;
	private String title;
	private String url;

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Topic [category=").append(category).append(", title=").append(title).append(", url=")
				.append(url).append("]");
		return builder.toString();
	}
}

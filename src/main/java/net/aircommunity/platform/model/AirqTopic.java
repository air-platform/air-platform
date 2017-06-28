package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * AirQ Topic
 * 
 * @author luocheng
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class AirqTopic implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String category;
	private final String title;
	private final String url;

	public AirqTopic(String category, String title, String url) {
		this.category = category;
		this.title = title;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
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

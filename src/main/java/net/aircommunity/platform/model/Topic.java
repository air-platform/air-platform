package net.aircommunity.platform.model;

import java.io.Serializable;

/**
 * Created by luocheng on 2017/4/17.
 */
public class Topic implements Serializable {
	private static final long serialVersionUID = 1L;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Topic [title=").append(title).append(", url=").append(url).append("]");
		return builder.toString();
	}
}

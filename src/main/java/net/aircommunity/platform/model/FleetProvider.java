package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * FleetProvider
 * 
 * @author Bin.Zhang
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class FleetProvider implements Serializable {
	private static final long serialVersionUID = 1L;

	// tenant id
	private final String id;

	// tenant display name
	private final String name;

	// tenant company logo
	private final String avatar;

	public FleetProvider(String id, String name, String avatar) {
		this.id = id;
		this.name = name;
		this.avatar = avatar;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAvatar() {
		return avatar;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetProvider [id=").append(id).append(", name=").append(name).append(", avatar=")
				.append(avatar).append("]");
		return builder.toString();
	}

}

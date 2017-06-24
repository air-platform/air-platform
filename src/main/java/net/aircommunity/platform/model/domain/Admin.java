package net.aircommunity.platform.model.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Admin model same as account.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_admin", indexes = { @Index(name = "idx_role", columnList = "role") })
@XmlAccessorType(XmlAccessType.FIELD)
public class Admin extends Account {
	private static final long serialVersionUID = 1L;

	public Admin() {
		super();
	}

	public Admin(String id) {
		super(id);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Admin [id=").append(id).append(", nickName=").append(nickName).append(", apiKey=")
				.append("******").append(", password=").append("******").append(", role=").append(role)
				.append(", status=").append(status).append(", avatar=").append(avatar).append(", creationDate=")
				.append(creationDate).append(", lastAccessedDate=").append(lastAccessedDate).append(", lastAccessedIp=")
				.append(lastAccessedIp).append("]");
		return builder.toString();
	}

}

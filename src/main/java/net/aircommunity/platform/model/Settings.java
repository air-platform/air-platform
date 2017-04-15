package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Database based configuration for AIR platform
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_settings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings extends Persistable {
	private static final long serialVersionUID = 1L;

	// configuration name
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	// configuration body
	@Lob
	@Column(name = "value")
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Settings [name=").append(name).append(", value=").append(value).append("]");
		return builder.toString();
	}
}

package net.aircommunity.platform.model.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
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
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "air_platfrom_settings", indexes = { @Index(name = "idx_category", columnList = "category") })
public class Settings extends Persistable {
	private static final long serialVersionUID = 1L;

	public static final String CATEGORY_SYSTEM = "system";
	public static final String CATEGORY_USER = "user";

	// configuration name
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	// configuration category
	@Column(name = "category", nullable = false)
	private String category;

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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
		builder.append("Settings [name=").append(name).append(", category=").append(category).append(", value=")
				.append(value).append("]");
		return builder.toString();
	}

	public static Settings newSystemSettings() {
		return newSettings(CATEGORY_SYSTEM);
	}

	public static Settings newUserSettings() {
		return newSettings(CATEGORY_USER);
	}

	public static Settings newSettings(String category) {
		Settings settings = new Settings();
		settings.category = Objects.requireNonNull(category, "category cannot be null");
		return settings;
	}
}

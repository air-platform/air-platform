package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * The basic knowledge of aviation managed by ADMIN.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_airclass")
@XmlAccessorType(XmlAccessType.FIELD)
public class AirClass extends Persistable {
	private static final long serialVersionUID = 1L;

	@Size(max = 255)
	@Column(name = "title", nullable = false, length = 255)
	private String title;

	@Lob
	@Column(name = "content")
	private String content;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date creationDate;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AirClass [title=").append(title).append(", content=").append(content).append(", creationDate=")
				.append(creationDate).append(", id=").append(id).append("]");
		return builder.toString();
	}

}

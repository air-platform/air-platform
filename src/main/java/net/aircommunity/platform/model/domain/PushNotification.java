package net.aircommunity.platform.model.domain;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.TenantAdapter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * Aircraft for (Taxi, Transportation, Tour) of an {@code Tenant}.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_push_notification")
@XmlAccessorType(XmlAccessType.FIELD)
public class PushNotification extends Persistable {
	private static final long serialVersionUID = 1L;

	@Lob
	@Column(name = "message")
	private String message;

	@Size(max = URL_LEN)
	@Column(name = "rich_text_link", length = URL_LEN)
	private String richTextLink;


	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = PUSH_NOTIFICATION_TYPE_LEN, nullable = false)
	private Type type;


	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = PUSH_NOTIFICATION_STATUS_LEN, nullable = false)
	private Status status;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	private Date creationDate;


	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "last_send_date")
	private Date lastSendDate;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getLastSendDate() {
		return lastSendDate;
	}

	public void setLastSendDate(Date lastSendDate) {
		this.lastSendDate = lastSendDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRichTextLink() {
		return richTextLink;
	}

	public void setRichTextLink(String richTextLink) {
		this.richTextLink = richTextLink;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public PushNotification() {
	}

	public PushNotification(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PushNotification [message=").append(message).append(", type=").append(type)
				.append(", richTextLink=").append(richTextLink).append(", id=").append(id).append("]");
		return builder.toString();
	}


	/**
	 * apron type
	 */
	public enum Type {
		PLAIN_TEXT, RICH_TEXT;

		public static Type fromString(String value) {
			for (Type e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}
	/**
	 * apron type
	 */
	public enum Status {
		NONE_PUSH, PUSH_SUCCESS, PUSH_FAILED;

		public static Status fromString(String value) {
			for (Status e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}
}

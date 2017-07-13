package net.aircommunity.platform.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.Email;
import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.DateTimeShortAdapter;

/**
 * Customer feedbacks.
 * 
 * @author Bin.Zhang
 */
public class Feedback implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String category;

	@NotEmpty
	private String person;

	@NotEmpty
	private String phone;

	@Email
	private String email;

	@XmlJavaTypeAdapter(DateTimeShortAdapter.class)
	private Date departureTime;

	@NotEmpty
	private String content;

	public String getCategory() {
		return category;
	}

	public String getPerson() {
		return person;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Feedback [category=").append(category).append(", person=").append(person).append(", phone=")
				.append(phone).append(", email=").append(email).append(", departureTime=").append(departureTime)
				.append(", content=").append(content).append("]");
		return builder.toString();
	}
}

package net.aircommunity.platform.model.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

import io.micro.annotation.constraint.Email;
import io.micro.annotation.constraint.Mobile;
import io.micro.common.Strings;
import net.aircommunity.platform.Constants;

/**
 * Contact information of an order.
 * 
 * @author Bin.Zhang
 */
@Embeddable
public class Contact implements Serializable, DomainConstants {
	private static final long serialVersionUID = 1L;

	@Size(max = PERSON_NAME_LEN)
	@Column(name = "contact_person", length = PERSON_NAME_LEN)
	private String person;

	@Mobile
	@Size(max = MOBILE_LEN)
	@Column(name = "contact_mobile", length = MOBILE_LEN)
	private String mobile;

	@Email
	@Size(max = EMAIL_LEN)
	@Column(name = "contact_email", length = EMAIL_LEN)
	private String email;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((mobile == null) ? 0 : mobile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		if (mobile == null) {
			if (other.mobile != null)
				return false;
		}
		else if (!mobile.equals(other.mobile))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Contact [person=").append(person).append(", mobile=").append(mobile).append(", email=")
				.append(email).append("]");
		return builder.toString();
	}

	/**
	 * Parse clientManagers for contacts.
	 * 
	 * @param clientManagers
	 * @return a set of Contacts
	 */
	public static Set<Contact> parseContacts(String clientManagers) {
		if (Strings.isBlank(clientManagers)) {
			return Collections.emptySet();
		}
		ImmutableSet.Builder<Contact> builder = ImmutableSet.builder();
		// XXX NOTE: will throw IllegalArgumentException when splitting if the format is not valid
		Map<String, String> contacts = Splitter.on(Constants.CONTACT_SEPARATOR).trimResults().omitEmptyStrings()
				.withKeyValueSeparator(Constants.CONTACT_INFO_SEPARATOR).split(clientManagers);
		contacts.forEach((person, email) -> {
			Contact contact = new Contact();
			contact.setEmail(email.trim());
			contact.setPerson(person.trim());
			builder.add(contact);
		});
		return builder.build();
	}

	/**
	 * Normalize clientManagers. MUST IN FORMAT OF: person1:email1, person2:email2, ..., personN:emailN
	 * 
	 * @param clientManagers
	 * @return normalized contacts
	 */
	public static String normalizeContacts(String clientManagers) {
		// XXX NOTE: will throw IllegalArgumentException when splitting if the format is not valid
		Map<String, String> contacts = Splitter.on(Constants.CONTACT_SEPARATOR).trimResults().omitEmptyStrings()
				.withKeyValueSeparator(Constants.CONTACT_INFO_SEPARATOR).split(clientManagers);
		return contacts
				.entrySet().stream().map(e -> new StringBuilder().append(e.getKey())
						.append(Constants.CONTACT_INFO_SEPARATOR).append(e.getValue()).toString())
				.collect(Collectors.joining(Constants.CONTACT_SEPARATOR));
	}

}

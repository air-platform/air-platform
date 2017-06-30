package net.aircommunity.platform.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.Mobile;
import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Passenger of an {@code User}
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_user_passenger", indexes = {
		@Index(name = "idx_user_id_identity", columnList = "user_id, identity", unique = true) })
@XmlAccessorType(XmlAccessType.FIELD)
public class Passenger extends Persistable implements Cloneable {
	private static final long serialVersionUID = 1L;

	// Passenger name
	@NotEmpty
	@Size(max = PERSON_NAME_LEN)
	@Column(name = "name", length = PERSON_NAME_LEN, nullable = false)
	private String name;

	// Passenger mobile
	@Mobile
	@NotEmpty
	@Size(max = MOBILE_LEN)
	@Column(name = "mobile", length = MOBILE_LEN)
	private String mobile;

	// e.g. ID Card number
	@NotEmpty
	@Size(max = IDENTITY_LEN)
	@Column(name = "identity", length = IDENTITY_LEN, nullable = false)
	private String identity;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@XmlJavaTypeAdapter(AccountAdapter.class)
	private User owner;

	public Passenger() {
	}

	public Passenger(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public Passenger clone() {
		try {
			return (Passenger) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identity == null) ? 0 : identity.hashCode());
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
		Passenger other = (Passenger) obj;
		if (identity == null) {
			if (other.identity != null)
				return false;
		}
		else if (!identity.equals(other.identity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Passenger [name=").append(name).append(", mobile=").append(mobile).append(", identity=")
				.append(identity).append(", id=").append(id).append("]");
		return builder.toString();
	}
}

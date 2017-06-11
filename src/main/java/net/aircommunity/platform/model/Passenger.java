package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import io.micro.annotation.constraint.NotEmpty;
import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Passenger of an {@code User}
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_user_passenger")
@XmlAccessorType(XmlAccessType.FIELD)
public class Passenger extends Persistable {
	private static final long serialVersionUID = 1L;

	// Passenger name
	@NotEmpty
	@Column(name = "name")
	private String name;

	// Passenger mobile
	@NotEmpty
	@Column(name = "mobile")
	private String mobile;

	// e.g. ID Card number
	@NotEmpty
	@Column(name = "identity", unique = true)
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

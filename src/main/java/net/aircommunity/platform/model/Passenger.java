package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.AccountAdapter;

/**
 * Passenger of an {@code Account}
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_user_passenger")
public class Passenger extends Persistable {
	private static final long serialVersionUID = 1L;

	// Passenger name
	@Column(name = "name")
	private String name;

	// Passenger mobile
	@Column(name = "mobile")
	private String mobile;

	// e.g. ID Card number
	@Column(name = "identity")
	private String identity;

	@XmlJavaTypeAdapter(AccountAdapter.class)
	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private User owner;

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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Passenger [name=").append(name).append(", mobile=").append(mobile).append(", identity=")
				.append(identity).append(", owner=").append(owner).append(", id=").append(id).append("]");
		return builder.toString();
	}
}

package net.aircommunity.platform.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * User (customer) profile.
 * 
 * @author Bin.Zhang
 */
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class User extends Account {
	private static final long serialVersionUID = 1L;

	// membership points
	@Column(name = "points")
	private long points;

	// membership level: e.g. member, silver, gold, platinum, diamond
	@Column(name = "rank")
	@Enumerated(EnumType.STRING)
	private Rank rank = Rank.MEMBER;

	@Column(name = "real_name")
	private String realName;

	@Column(name = "gender")
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Temporal(value = TemporalType.DATE)
	@XmlJavaTypeAdapter(DateAdapter.class)
	@XmlElement
	@Column(name = "birthday")
	private Date birthday;

	// in format of: province:city
	@Column(name = "city")
	private String city;

	// multiple hobbies with comma(,) separated, e.g. travel, shopping
	@Column(name = "hobbies")
	private String hobbies;

	// Bidirectional
	@XmlTransient
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", orphanRemoval = true)
	private List<Address> addresses = new ArrayList<>();

	// Bidirectional
	@XmlTransient
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", orphanRemoval = true)
	private List<Passenger> passengers = new ArrayList<>();

	@PrePersist
	private void beforeSave() {
		role = Role.USER;
	}

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHobbies() {
		return hobbies;
	}

	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void addAddress(Address address) {
		if (address != null) {
			address.setOwner(this);
			addresses.add(address);
		}
	}

	public void removeAddress(Address address) {
		if (address != null) {
			addresses.remove(address);
		}
	}

	public void removeAddressById(String addressId) {
		if (addressId != null) {
			Iterator<Address> iter = addresses.iterator();
			while (iter.hasNext()) {
				Address addr = iter.next();
				if (addr.getId().equals(addressId)) {
					iter.remove();
				}
			}
		}
	}

	public List<Passenger> getPassengers() {
		return passengers;
	}

	public void addPassenger(Passenger passenger) {
		if (passenger != null) {
			passenger.setOwner(this);
			passengers.add(passenger);
		}
	}

	public void removePassenger(Passenger passenger) {
		if (passenger != null) {
			passengers.remove(passenger);
		}
	}

	public void removePassengerById(String passengerId) {
		if (passengerId != null) {
			Iterator<Passenger> iter = passengers.iterator();
			while (iter.hasNext()) {
				Passenger passenger = iter.next();
				if (passenger.getId().equals(passengerId)) {
					iter.remove();
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [points=").append(points).append(", rank=").append(rank).append(", realName=")
				.append(realName).append(", gender=").append(gender).append(", birthday=").append(birthday)
				.append(", city=").append(city).append(", hobbies=").append(hobbies).append(", addresses=")
				.append(addresses).append(", passengers=").append(passengers).append(", nickName=").append(nickName)
				.append(", role=").append(role).append(", status=").append(status).append(", creationDate=")
				.append(creationDate).append(", avatar=").append(avatar).append(", id=").append(id).append("]");
		return builder.toString();
	}

	/**
	 * User {@code Gender}.
	 */
	public enum Gender {
		MALE, FEMALE;
	}

	/**
	 * User {@code Rank}.
	 */
	public enum Rank {
		MEMBER, SILVER, GOLD, PLATINUM, DIAMOND;
	}

}

package net.aircommunity.platform.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
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

	// cannot simultaneously fetch multiple bags when >=2 enteties FetchType.EAGER
	// @ElementCollection(fetch = FetchType.EAGER)
	// FIXME
	@XmlTransient
	@ElementCollection
	@CollectionTable(name = "air_platfrom_account_address", joinColumns = @JoinColumn(name = "account_id"))
	private List<Address> addresses = new ArrayList<>();

	@XmlTransient
	@ElementCollection
	@CollectionTable(name = "air_platfrom_account_passenger", joinColumns = @JoinColumn(name = "account_id"))
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

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public List<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [points=").append(points).append(", rank=").append(rank).append(", realName=")
				.append(realName).append(", gender=").append(gender).append(", birthday=").append(birthday)
				.append(", city=").append(city).append(", hobbies=").append(hobbies).append("]");
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

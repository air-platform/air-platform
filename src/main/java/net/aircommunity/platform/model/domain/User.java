package net.aircommunity.platform.model.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.DateAdapter;

/**
 * User model (allow to login to APPs).
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_user", indexes = { @Index(name = "idx_role", columnList = "role") })
public class User extends Account {
	private static final long serialVersionUID = 1L;

	// membership points
	@Min(0)
	@Column(name = "points")
	private long points = 0;

	// membership level: e.g. member, silver, gold, platinum, diamond
	@Column(name = "rank", length = 10)
	@Enumerated(EnumType.STRING)
	private Rank rank = Rank.MEMBER;

	@Size(max = 255)
	@Column(name = "real_name")
	private String realName;

	// ID card
	@Size(max = 255)
	@Column(name = "identity")
	private String identity;

	@Column(name = "gender", length = 8)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Temporal(value = TemporalType.DATE)
	@XmlJavaTypeAdapter(DateAdapter.class)
	@Column(name = "birthday")
	private Date birthday;

	@Size(max = 255)
	@Column(name = "country")
	private String country;

	// state or province
	@Size(max = 255)
	@Column(name = "province")
	private String province;

	@Size(max = 255)
	@Column(name = "city")
	private String city;

	// multiple hobbies with comma(,) separated, e.g. travel, shopping
	@Lob
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

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "dailysignin_id")
	private DailySignin dailySignin;

	public User() {
		super();
	}

	public User(String id) {
		super(id);
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

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
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

	public Passenger removePassengerById(String passengerId) {
		if (passengerId != null) {
			Iterator<Passenger> iter = passengers.iterator();
			while (iter.hasNext()) {
				Passenger passenger = iter.next();
				if (passenger.getId().equals(passengerId)) {
					passenger.setOwner(null);
					iter.remove();
					return passenger;
				}
			}
		}
		return null;
	}

	public DailySignin getDailySignin() {
		return dailySignin;
	}

	public void setDailySignin(DailySignin dailySignin) {
		this.dailySignin = dailySignin;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [points=").append(points).append(", rank=").append(rank).append(", realName=")
				.append(realName).append(", identity=").append(identity).append(", gender=").append(gender)
				.append(", birthday=").append(birthday).append(", country=").append(country).append(", province=")
				.append(province).append(", city=").append(city).append(", hobbies=").append(hobbies)
				.append(", nickName=").append(nickName).append(", role=").append(role).append(", status=")
				.append(status).append(", creationDate=").append(creationDate).append(", avatar=").append(avatar)
				.append(", lastAccessedIp=").append(lastAccessedIp).append(", id=").append(id).append("]");
		return builder.toString();
	}

	/**
	 * User {@code Gender}.
	 */
	public enum Gender {
		MALE, FEMALE, OTHER;

		public static Gender fromString(String value) {
			for (Gender e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}

	/**
	 * User {@code Rank}.
	 */
	public enum Rank {
		MEMBER, SILVER, GOLD, PLATINUM, DIAMOND;

		public static Rank fromString(String value) {
			for (Rank e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}

}

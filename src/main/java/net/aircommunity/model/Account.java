package net.aircommunity.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Account profile.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_account")
@XmlAccessorType(XmlAccessType.FIELD)
public class Account extends Persistable {
	private static final long serialVersionUID = 1L;

	// NOTE: may useful for role TENANT and ADMIN
	@Column(name = "api_key", nullable = false, unique = true)
	private String apiKey;

	@XmlTransient
	@NotNull
	@Column(name = "password", nullable = false)
	private String password;

	@NotNull
	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role = Role.USER;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.ENABLED;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	private Date creationDate;

	// membership points
	@Column(name = "points")
	private long points;

	// membership level: e.g. member, silver, gold, platinum, diamond
	@Column(name = "rank")
	@Enumerated(EnumType.STRING)
	private Rank rank = Rank.MEMBER;

	// user profile
	@Column(name = "real_name")
	private String realName;

	@Column(name = "gender")
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Temporal(value = TemporalType.DATE)
	@Column(name = "birthday")
	private Date birthday;

	// in format of: province:city
	@Column(name = "city")
	private String city;

	@Column(name = "avatar")
	private String avatar;

	// multiple hobbies with comma(,) separated, e.g. travel, shopping
	@Column(name = "hobbies")
	private String hobbies;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getHobbies() {
		return hobbies;
	}

	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
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

	/**
	 * An {@code Status} represents the various states an account may be in.
	 */
	public enum Status {

		/**
		 * An enabled account may login to applications.
		 */
		ENABLED,

		/**
		 * A disabled account may not login to applications.
		 */
		DISABLED,

		/**
		 * An unverified account is a disabled account that does not have a verified email address.
		 */
		UNVERIFIED
	}

}

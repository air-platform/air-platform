package net.aircommunity.platform.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
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
@Inheritance
@Table(name = "air_platfrom_account")
@XmlAccessorType(XmlAccessType.FIELD)
public class Account extends Persistable {
	private static final long serialVersionUID = 1L;

	// display purpose
	@Column(name = "nick_name")
	protected String nickName;

	// NOTE: may useful for role TENANT and ADMIN
	@XmlTransient
	@Column(name = "api_key", nullable = false, unique = true)
	protected String apiKey;

	@XmlTransient
	@Column(name = "password", nullable = false)
	protected String password;

	@NotNull
	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	protected Role role;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	protected Status status = Status.ENABLED;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	protected Date creationDate;

	@Column(name = "avatar")
	protected String avatar;

	// @OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<AccountAuth> auths;

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Account [nickName=").append(nickName).append(", role=").append(role).append(", status=")
				.append(status).append(", creationDate=").append(creationDate).append(", avatar=").append(avatar)
				.append(", id=").append(id).append("]");
		return builder.toString();
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

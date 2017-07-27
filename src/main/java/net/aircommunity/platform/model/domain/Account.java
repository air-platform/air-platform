package net.aircommunity.platform.model.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Basic account model of the platform. (need make it abstract, otherwise we cannot query via inheritance, find an given
 * accountId, it will find the right model from {@code Account} subclasses)
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Account extends Persistable {
	private static final long serialVersionUID = 1L;

	// display purpose
	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "nick_name")
	protected String nickName;

	// XXX NOTE: useful only for role TENANT and ADMIN
	@XmlTransient
	@Size(max = ACCOUNT_APIKEY_LEN)
	@Column(name = "api_key", length = ACCOUNT_APIKEY_LEN, nullable = false, unique = true)
	protected String apiKey;

	// why password here, because username, mobile, email etc. internal auth will share the same password
	@XmlTransient
	@Size(max = ACCOUNT_PASSWORD_LEN)
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "password", length = ACCOUNT_PASSWORD_LEN, nullable = false)
	protected String password;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "role", length = ACCOUNT_ROLE_LEN, nullable = false)
	protected Role role;

	@Column(name = "status", length = ACCOUNT_STATUS_LEN, nullable = false)
	@Enumerated(EnumType.STRING)
	protected Status status = Status.ENABLED;

	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "avatar")
	protected String avatar;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date creationDate;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "last_accessed_date")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date lastAccessedDate;

	// last accessed IP regardless from which source (AUTH type)
	// just duplicated AccountAuth lastAccessedIp for quick lookup
	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "last_accessed_ip")
	@JsonView(JsonViews.Admin.class)
	protected String lastAccessedIp;

	// @OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<AccountAuth> auths;

	public Account() {
	}

	public Account(String id) {
		this.id = id;
	}

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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastAccessedDate() {
		return lastAccessedDate;
	}

	public void setLastAccessedDate(Date lastAccessedDate) {
		this.lastAccessedDate = lastAccessedDate;
	}

	public String getLastAccessedIp() {
		return lastAccessedIp;
	}

	public void setLastAccessedIp(String lastAccessedIp) {
		this.lastAccessedIp = lastAccessedIp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Account [id=").append(id).append(", nickName=").append(nickName).append(", apiKey=")
				.append("******").append(", password=").append("******").append(", role=").append(role)
				.append(", status=").append(status).append(", avatar=").append(avatar).append(", creationDate=")
				.append(creationDate).append(", lastAccessedDate=").append(lastAccessedDate).append(", lastAccessedIp=")
				.append(lastAccessedIp).append("]");
		return builder.toString();
	}

	/**
	 * An {@code Type} represents the various types an account may be in. One account type may have many roles, except
	 * currently defined roles.
	 */
	public enum Type {
		USER, TENANT, ADMIN;

		public static Type fromString(String value) {
			for (Type e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
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
		 * Expired may useful in future
		 */
		// EXPIRED,

		/**
		 * An lock account can login, but cannot place orders.
		 */
		LOCKED,

		/**
		 * A disabled account may not login to applications.
		 */
		DISABLED;

		public static Status fromString(String value) {
			for (Status e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}

}

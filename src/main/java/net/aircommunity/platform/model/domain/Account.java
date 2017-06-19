package net.aircommunity.platform.model.domain;

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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonView;

import net.aircommunity.platform.model.JsonViews;
import net.aircommunity.platform.model.Role;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Account model of the platform.
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
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	protected Date creationDate;

	@Column(name = "avatar")
	protected String avatar;

	// last accessed IP regardless from which source (AUTH type)
	// just duplicated AccountAuth lastAccessedIp for quick lookup
	@Column(name = "last_accessed_ip")
	@JsonView({ JsonViews.Admin.class, JsonViews.Tenant.class })
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

	public String getLastAccessedIp() {
		return lastAccessedIp;
	}

	public void setLastAccessedIp(String lastAccessedIp) {
		this.lastAccessedIp = lastAccessedIp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Account [nickName=").append(nickName).append(", apiKey=").append("*****").append(", role=")
				.append(role).append(", status=").append(status).append(", creationDate=").append(creationDate)
				.append(", avatar=").append(avatar).append(", id=").append(id).append("]");
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
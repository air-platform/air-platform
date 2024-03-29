package net.aircommunity.platform.model.domain;

import java.util.Date;
import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
import net.aircommunity.platform.model.jaxb.AccountAdapter;
import net.aircommunity.platform.model.jaxb.DateTimeAdapter;

/**
 * Account authentication info (support multiple authentication methods).
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platform_auth", indexes = {
		// one same auth type per account
		@Index(name = "idx_account_id_type", columnList = "account_id, type", unique = true),
		// unique principal per auth type
		@Index(name = "idx_type_principal", columnList = "type, principal", unique = true) //
})
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountAuth extends Persistable {
	private static final long serialVersionUID = 1L;

	public static final long EXPIRES_NERVER = -1;
	public static final long EXPIRES_IN_ONE_DAY = 24 * 60 * 60;

	// e.g. username/email/mobile/wechat/qq/weibo etc.
	@NotNull
	@Column(name = "type", length = ACCOUNT_AUTHTYPE_LEN, nullable = false)
	@Enumerated(EnumType.STRING)
	private AuthType type;

	@NotNull
	@Size(max = ACCOUNT_PRINCIPAL_LEN)
	@Column(name = "principal", length = ACCOUNT_PRINCIPAL_LEN, nullable = false)
	private String principal;

	@XmlTransient
	@Size(max = ACCOUNT_CREDENTIAL_LEN)
	@Column(name = "credential", length = ACCOUNT_CREDENTIAL_LEN)
	private String credential;

	// expires in seconds, define the lifespan of this auth
	// only useful for 3rd party auth like wechat/qq/weibo etc.
	@Column(name = "expires_in")
	private long expires;

	@Column(name = "verified")
	private boolean verified;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "creation_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date creationDate;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "last_accessed_date", nullable = false)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date lastAccessedDate;

	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "last_accessed_ip")
	@JsonView(JsonViews.Admin.class)
	private String lastAccessedIp;

	// user-agent, from which source: app, browser etc.
	@Size(max = DEFAULT_FIELD_LEN)
	@Column(name = "last_accessed_source")
	@JsonView(JsonViews.Admin.class)
	private String lastAccessedSource;

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	@XmlJavaTypeAdapter(AccountAdapter.class)
	private Account account;

	public AuthType getType() {
		return type;
	}

	public void setType(AuthType type) {
		this.type = type;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
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

	public String getLastAccessedSource() {
		return lastAccessedSource;
	}

	public void setLastAccessedSource(String lastAccessedSource) {
		this.lastAccessedSource = lastAccessedSource;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountAuth [type=").append(type).append(", principal=").append(principal)
				.append(", credential=******").append(", expires=").append(expires).append(", verified=")
				.append(verified).append(", creationDate=").append(creationDate).append(", lastAccessedDate=")
				.append(lastAccessedDate).append(", lastAccessedIp=").append(lastAccessedIp)
				.append(", lastAccessedSource=").append(lastAccessedSource).append(", id=").append(id).append("]");
		return builder.toString();
	}

	/**
	 * Authentication types.
	 */
	public enum AuthType {
		USERNAME, EMAIL, MOBILE, WECHAT, QQ, WEIBO;

		public static EnumSet<AuthType> INTERNAL_AUTHS = EnumSet
				.copyOf(Stream.of(AuthType.values()).filter(AuthType::isInternal).collect(Collectors.toSet()));

		public boolean isInternal() {
			return this == USERNAME || this == EMAIL || this == MOBILE;
		}

		public static AuthType fromString(String value) {
			for (AuthType e : values()) {
				if (e.name().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}

}

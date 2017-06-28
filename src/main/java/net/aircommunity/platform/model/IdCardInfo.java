package net.aircommunity.platform.model;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.concurrent.Immutable;

import net.aircommunity.platform.model.domain.User.Gender;

/**
 * ID Card information holder.
 * 
 * @author Bin.Zhang
 */
@Immutable
public class IdCardInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String identity;
	private final String name;
	private final String address;
	private final Date birthday;
	private final Gender gender;

	public IdCardInfo(String identity, String name, String address, Date birthday, Gender gender) {
		this.identity = identity;
		this.name = name;
		this.address = address;
		this.birthday = birthday;
		this.gender = gender;
	}

	public String getIdentity() {
		return identity;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public Date getBirthday() {
		return birthday;
	}

	public Gender getGender() {
		return gender;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IdCardInfo [identity=").append(identity).append(", name=").append(name).append(", address=")
				.append(address).append(", birthday=").append(birthday).append(", gender=").append(gender).append("]");
		return builder.toString();
	}

}

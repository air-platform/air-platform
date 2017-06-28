package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.Username;

/**
 * Username update request.
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class UsernameRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Username
	private String username;

	public String getUsername() {
		return username;
	}

}

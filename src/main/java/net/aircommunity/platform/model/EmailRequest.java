package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.annotation.constraint.Email;

/**
 * Email update request.
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Email
	@NotNull
	private String email;

	public String getEmail() {
		return email;
	}

}

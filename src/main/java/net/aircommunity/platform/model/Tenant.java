package net.aircommunity.platform.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Tenant profile.
 * 
 * @author Bin.Zhang
 */
@Entity
@Table(name = "air_platfrom_tenant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tenant extends Persistable {
	private static final long serialVersionUID = 1L;

	private String name;

	// contact address
	private String address;

	// contact hot-line
	private String hotline;

	// detail description
	private String description;

}

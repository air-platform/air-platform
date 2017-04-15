package net.aircommunity.platform.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Vendor Aware Order model.
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class VendorAwareOrder extends Order {
	private static final long serialVersionUID = 1L;

	// NOTE: TODO (create a new VendorAwareOrder extends Order, e.g. coz CharterOrder will have multi-vendors)?
	// extra info (the vendor of this card, it's already available in jetCard, just add extra id to avoid join)
	// should be set via Product.vendor
	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	protected Tenant vendor;

}

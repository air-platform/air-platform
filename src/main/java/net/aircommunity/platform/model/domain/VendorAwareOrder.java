package net.aircommunity.platform.model.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Single vendor {@code Tenant} order model.
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class VendorAwareOrder extends StandardOrder {
	private static final long serialVersionUID = 1L;

	// NOTE: VendorAwareOrder extends StandardOrder, because CharterOrder will have multiple-vendors
	// extra info (e.g. the vendor of this product, it's already available in product, just add extra id to avoid join)
	// should be set via Product.vendor
	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tenant_id", nullable = false)
	protected Tenant vendor;

	@Override
	public final void setProduct(Product product) {
		vendor = product.getVendor();
		doSetProduct(product);
	}

	protected abstract void doSetProduct(Product product);

	// XXX doesn't work from REST API:
	// com.fasterxml.jackson.databind.JsonMappingException: Can not construct instance of
	// net.aircommunity.platform.model.Product: abstract types either need to be mapped to concrete types, have custom
	// deserializer, or contain additional type information
	// at [Source: io.undertow.servlet.spec.ServletInputStreamImpl@32ec2489; line: 9, column: 14] (through reference
	// chain: net.aircommunity.platform.model.FerryFlightOrder["product"])
	// at com.fasterxml.jackson.databind.JsonMappingException.from(JsonMappingException.java:270)

}

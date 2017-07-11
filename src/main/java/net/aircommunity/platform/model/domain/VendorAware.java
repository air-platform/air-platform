package net.aircommunity.platform.model.domain;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.jaxb.TenantAdapter;

/**
 * Vendor aware entity
 * 
 * @author Bin.Zhang
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class VendorAware extends Persistable {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "tenant_id", nullable = false)
	@XmlJavaTypeAdapter(TenantAdapter.class)
	protected Tenant vendor;

	public Tenant getVendor() {
		return vendor;
	}

	public void setVendor(Tenant vendor) {
		this.vendor = vendor;
	}

	/**
	 * Test if current entity is the owner of the given tenant.
	 */
	public boolean isOwner(String tenantId) {
		return vendor.getId().equals(tenantId);
	}

}

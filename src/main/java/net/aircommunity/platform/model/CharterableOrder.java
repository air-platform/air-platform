package net.aircommunity.platform.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Charterable Vendor Aware Order model.
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CharterableOrder extends VendorAwareOrder {
	private static final long serialVersionUID = 1L;

	// true: 包机, false: 拼座
	@Column(name = "chartered", nullable = false)
	protected boolean chartered;

	public boolean isChartered() {
		return chartered;
	}

	public void setChartered(boolean chartered) {
		this.chartered = chartered;
	}
}

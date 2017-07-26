package net.aircommunity.platform.model.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * SalesPackage Product with multiple-prices (sales packages).
 * 
 * @author Bin.Zhang
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class SalesPackageProduct extends Product {
	private static final long serialVersionUID = 1L;

	// FIXME: WHY SortedSet not working on server (works on local machine), it should be mapped to PersistentSortedSet
	// instance of PersistentSet
	// BUT: we got exception:
	// ClassCastException: cannot assign instance of org.hibernate.collection.internal.PersistentSet to field
	// net.aircommunity.platform.model.domain.SalesPackageProduct.salesPackages of type java.util.SortedSet
	// protected SortedSet<SalesPackage> salesPackages = new TreeSet<>();
	// @OrderBy(" rank DESC")
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	protected Set<SalesPackage> salesPackages = new HashSet<>();

	public Set<SalesPackage> getSalesPackages() {
		return salesPackages;
	}

	public void setSalesPackages(Set<SalesPackage> salesPackages) {
		if (salesPackages != null) {
			salesPackages.stream().forEach(item -> item.setProduct(this));
			this.salesPackages.clear();
			this.salesPackages.addAll(salesPackages);
		}
	}

	public boolean addSalesPackage(SalesPackage item) {
		if (salesPackages.contains(item)) {
			return false;
		}
		item.setProduct(this);
		return salesPackages.add(item);
	}

	public boolean removeSalesPackage(SalesPackage item) {
		if (!salesPackages.contains(item)) {
			return false;
		}
		return salesPackages.remove(item);
	}
}
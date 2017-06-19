package net.aircommunity.platform.model.domain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.micro.common.Strings;

/**
 * Instalment order model allow payment by instalments. (NOT USED FOR ATM)
 * 
 * @author Bin.Zhang
 */
// @Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class InstalmentOrder extends Order {
	private static final long serialVersionUID = 1L;

	// Bidirectional
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
	protected Set<Instalment> instalments = new HashSet<>();

	public boolean isPaidFully() {
		return instalments.stream().allMatch(Instalment::isPaid);
	}

	public Set<Instalment> getInstalments() {
		return instalments;
	}

	public void setInstalments(Set<Instalment> instalments) {
		if (instalments != null) {
			instalments.stream().forEach(instalment -> instalment.setOrder(this));
			this.instalments.clear();
			this.instalments = instalments;
		}
	}

	public Optional<Instalment> findInstalment(String instalmentId) {
		if (Strings.isBlank(instalmentId)) {
			return Optional.empty();
		}
		return instalments.stream().filter(instalment -> instalment.id.equals(instalments)).findFirst();
	}

	public void addInstalment(Instalment instalment) {
		if (instalment != null) {
			instalment.setOrder(this);
			instalments.add(instalment);
		}
	}

	public void removeInstalment(Instalment instalment) {
		if (instalment != null) {
			instalments.remove(instalment);
		}
	}

}

package net.aircommunity.platform.model;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.model.domain.Tenant;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.model.jaxb.TenantAdapter;

/**
 * Product summary info.
 * 
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductSummary implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String id;
	private final String name;
	private final String image;
	private final Category category;
	@XmlJavaTypeAdapter(TenantAdapter.class)
	private final Tenant vendor;

	public ProductSummary(Product product) {
		id = product.getId();
		name = product.getName();
		category = product.getCategory();
		image = product.getImage();
		vendor = product.getVendor();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public Category getCategory() {
		return category;
	}

	public Tenant getVendor() {
		return vendor;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductSummary [id=").append(id).append(", name=").append(name).append(", image=").append(image)
				.append(", category=").append(category).append("]");
		return builder.toString();
	}

}

package net.aircommunity.platform.model.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.domain.ProductFamily;
import net.aircommunity.platform.model.jaxb.ProductFamilyAdapter.ProductFamilyView;

/**
 * Adapt a ProductFamily object to a simple id & name
 * 
 * @author Bin.Zhang
 */
public class ProductFamilyAdapter extends XmlAdapter<ProductFamilyView, ProductFamily> {

	@Override
	public ProductFamilyView marshal(ProductFamily family) throws Exception {
		return new ProductFamilyView(family.getId(), family.getName());
	}

	@Override
	public ProductFamily unmarshal(ProductFamilyView family) throws Exception {
		return new ProductFamily(family.getId());
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ProductFamilyView {
		private String id;
		private String name;

		public ProductFamilyView() {
		}

		public ProductFamilyView(String id) {
			this(id, null);
		}

		public ProductFamilyView(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

}
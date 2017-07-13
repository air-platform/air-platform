package net.aircommunity.platform.model;

import java.io.Serializable;

import io.micro.annotation.constraint.NotEmpty;

/**
 * Point rule.
 * 
 * @author Bin.Zhang
 */
public class PointRule implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String name;

	private long value;

	public PointRule() {
	}

	public PointRule(String name, long value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointRule other = (PointRule) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PointRule [name=").append(name).append(", value=").append(value).append("]");
		return builder.toString();
	}
}

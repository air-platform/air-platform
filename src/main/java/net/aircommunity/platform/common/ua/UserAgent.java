package net.aircommunity.platform.common.ua;

import java.util.Map;

/**
 * User Agent parsed data class
 */
public class UserAgent {
	public static final UserAgent OTHER = new UserAgent("Other", null, null, null);

	public final String family;
	public final String major;
	public final String minor;
	public final String patch;

	public UserAgent(String family, String major, String minor, String patch) {
		this.family = family;
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public static UserAgent fromMap(Map<String, String> m) {
		return new UserAgent(m.get("family"), m.get("major"), m.get("minor"), m.get("patch"));
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof UserAgent))
			return false;

		UserAgent o = (UserAgent) other;
		return ((this.family != null && this.family.equals(o.family)) || this.family == o.family)
				&& ((this.major != null && this.major.equals(o.major)) || this.major == o.major)
				&& ((this.minor != null && this.minor.equals(o.minor)) || this.minor == o.minor)
				&& ((this.patch != null && this.patch.equals(o.patch)) || this.patch == o.patch);
	}

	@Override
	public int hashCode() {
		int h = family == null ? 0 : family.hashCode();
		h += major == null ? 0 : major.hashCode();
		h += minor == null ? 0 : minor.hashCode();
		h += patch == null ? 0 : patch.hashCode();
		return h;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserAgent [family=").append(family).append(", major=").append(major).append(", minor=")
				.append(minor).append(", patch=").append(patch).append("]");
		return builder.toString();
	}
}
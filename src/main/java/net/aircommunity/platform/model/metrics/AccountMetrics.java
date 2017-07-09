package net.aircommunity.platform.model.metrics;

import java.io.Serializable;

/**
 * Account metrics.
 * 
 * @author Bin.Zhang
 */
public class AccountMetrics implements Serializable {
	private static final long serialVersionUID = 1L;

	private long totalCount;
	private long adminCount;
	private long tenantCount;
	private long userCount;
	// new user registrations this month
	private long userCountThisMonth;

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getAdminCount() {
		return adminCount;
	}

	public void setAdminCount(long adminCount) {
		this.adminCount = adminCount;
	}

	public long getTenantCount() {
		return tenantCount;
	}

	public void setTenantCount(long tenantCount) {
		this.tenantCount = tenantCount;
	}

	public long getUserCount() {
		return userCount;
	}

	public void setUserCount(long userCount) {
		this.userCount = userCount;
	}

	public long getUserCountThisMonth() {
		return userCountThisMonth;
	}

	public void setUserCountThisMonth(long userCountThisMonth) {
		this.userCountThisMonth = userCountThisMonth;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountMetrics [totalCount=").append(totalCount).append(", adminCount=").append(adminCount)
				.append(", tenantCount=").append(tenantCount).append(", userCount=").append(userCount)
				.append(", userCountThisMonth=").append(userCountThisMonth).append("]");
		return builder.toString();
	}

}

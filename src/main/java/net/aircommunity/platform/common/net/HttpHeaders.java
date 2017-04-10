package net.aircommunity.platform.common.net;

import net.aircommunity.platform.model.Page;

/**
 * Handy HTTP headers utilities for JAXRS.
 * 
 * @author Bin.Zhang
 */
public abstract class HttpHeaders implements javax.ws.rs.core.HttpHeaders {
	public static final String HEADER_AVAILABLE = "Available";
	public static final String HEADER_PAGINATION = "Pagination";

	private static final String FILE_ATTACHMENT_FORMAT = "attachment; filename = %s";
	private static final String HEADER_PAGINATION_VALUE_FORMAT = "totalPages=%d,page=%d,pageSize=%d,totalRecords=%d,"
			+ "isFirstPage=%b,isLastPage=%b,hasPreviousPage=%b,hasNextPage=%b,hasContent=%b";

	/**
	 * Build content disposition HTTP HEADER.
	 * 
	 * @param fileName the file name
	 * @return content disposition
	 */
	public static String disposition(String fileName) {
		return String.format(FILE_ATTACHMENT_FORMAT, fileName);
	}

	/**
	 * Build pagination info HTTP HEADER.
	 * 
	 * @param page the page model
	 * @return pagination
	 */
	public static String pagination(Page<?> page) {
		return String.format(HEADER_PAGINATION_VALUE_FORMAT, page.getTotalPages(), page.getPage()/* start from 1 */,
				page.getPageSize(), page.getTotalRecords(), page.isFirstPage(), page.isLastPage(),
				page.hasPreviousPage(), page.hasNextPage(), page.hasContent());
	}

	private HttpHeaders() {
		throw new AssertionError();
	}
}

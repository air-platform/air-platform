package net.aircommunity.service.internal;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import net.aircommunity.model.Page;

/**
 * Pagination support
 * 
 * @author Bin.Zhang
 */
final class Pages {

	/**
	 * Default page size.
	 */
	public static final int DEFAULT_PAGE_SIZE = 10;

	/**
	 * @param page 1-indexed
	 * @param pageSize
	 * @return
	 */
	public static Pageable createPageRequest(int page, int pageSize) {
		// our page start from index= 1
		if (pageSize <= 0) {
			pageSize = DEFAULT_PAGE_SIZE;
		}
		if (page <= 0) {
			page = 1;
		}
		// NOTE: PageRequest's pages are zero indexed
		return new PageRequest(page - 1, pageSize);
	}

	/**
	 * @param page 1-indexed
	 * @param pageSize
	 * @return
	 */
	public static <T> Page<T> createEmptyPage(int page, int pageSize) {
		return new Page<T>(page, pageSize, 0, Collections.emptyList());
	}

	/**
	 * @param page 1-indexed
	 * @param pageSize
	 * @param total
	 * @param content
	 * @return
	 */
	public static <T> Page<T> createPage(int page, int pageSize, long total, List<T> content) {
		return new Page<T>(page, pageSize, total, content);
	}

	/**
	 * @param pageable
	 * @param total
	 * @param content
	 * @return
	 */
	public static <T> Page<T> createPage(Pageable pageable, long total, List<T> content) {
		// NOTE: Spring domain pages are zero indexed
		return new Page<T>(pageable.getPageNumber() + 1, pageable.getPageSize(), total, content);
	}

	/**
	 * Adapt spring page model to our page model
	 * 
	 * @param page
	 * @return the adpated page
	 */
	public static <T> Page<T> adapt(org.springframework.data.domain.Page<T> page) {
		// NOTE: Spring domain pages are zero indexed
		return new Page<T>(page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getContent());
	}

	private Pages() {
		throw new AssertionError();
	}

}

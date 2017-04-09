package net.aircommunity.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * A page is a sublist of a list of objects. It allows gain information about the position of it in the containing
 * entire list.
 * 
 * @param <T> the type of which the page consists.
 * @author Bin.Zhang
 */
@Immutable
@XmlAccessorType(XmlAccessType.NONE)
public class Page<T> implements Iterable<T>, Serializable {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public static final Page EMPTY = new Page();

	// Page are 1 indexed
	private final int page;
	private final int pageSize;
	private final long totalRecords;
	private final List<T> content = new ArrayList<T>();

	/**
	 * Page with empty content.
	 * 
	 * @param page
	 * @param pageSize
	 * @return empty page
	 */
	public static <T> Page<T> emptyPage(int page, int pageSize) {
		return new Page<T>(page, pageSize);
	}

	/**
	 * @param page
	 * @param pageSize
	 * @param totalRecords
	 * @param content
	 */
	public Page(int page, int pageSize, long totalRecords, List<T> content) {
		checkArguments(content != null, "Content must not be null!");
		// checkArguments(page > 0, "page must be >0!");
		// checkArguments(pageSize > 0, "pageSize must be >0!");
		checkArguments(totalRecords >= 0, "totalRecords must be >=0!");
		//
		this.content.addAll(content);
		this.page = page <= 0 ? 1 : page;
		this.pageSize = pageSize <= 0 ? 1 : pageSize;
		this.totalRecords = totalRecords;
	}

	// default construct required for serialization
	Page() {
		this(1, 1);
	}

	Page(int page, int pageSize) {
		this(page, pageSize, 0, Collections.emptyList());
	}

	/**
	 * Returns the number of the current page. Is always non-negative and less than {@code Page#getTotalPages()}.
	 * 
	 * @return the number of the current page (NOT zero indexed, start from 1)
	 */
	@XmlElement
	public int getPage() {
		return page;
	}

	/**
	 * Returns the size of the page.
	 * 
	 * @return the size of the page
	 */
	@XmlElement
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Returns the number of total pages.
	 * 
	 * @return the number of total pages
	 */
	@XmlElement
	public int getTotalPages() {
		return getPageSize() == 0 ? 0 : (int) Math.ceil((double) totalRecords / (double) getPageSize());
	}

	/**
	 * Returns the number of records currently on this page.
	 * 
	 * @return the number of records currently on this page
	 */
	@XmlElement
	public int getRecords() {
		return content.size();
	}

	/**
	 * Returns the total amount of records.
	 * 
	 * @return the total amount of records
	 */
	@XmlElement
	public long getTotalRecords() {
		return totalRecords;
	}

	/**
	 * Returns the view of page content (read-only content)
	 * 
	 * @return the view of page content (read-only content)
	 */
	@XmlElement
	public List<T> getContent() {
		return Collections.unmodifiableList(content);
	}

	/**
	 * Returns if there is a previous page.
	 * 
	 * @return if there is a previous page
	 */
	@XmlElement(name = "hasPreviousPage")
	public boolean hasPreviousPage() {
		return getPage() > 1; // page start from 1
	}

	/**
	 * Returns whether the current page is the first one.
	 * 
	 * @return true if first page, false otherwise
	 */
	@XmlElement(name = "isFirstPage")
	public boolean isFirstPage() {
		return !hasPreviousPage();
	}

	/**
	 * Returns if there is a next page.
	 * 
	 * @return if there is a next page
	 */
	@XmlElement(name = "hasNextPage")
	public boolean hasNextPage() {
		return (getPage() - 1 + 1) * getPageSize() < totalRecords;
	}

	/**
	 * Returns whether the current page is the last one.
	 * 
	 * @return true if last page, false otherwise
	 */
	@XmlElement(name = "isLastPage")
	public boolean isLastPage() {
		return !hasNextPage();
	}

	/**
	 * Returns whether the {@link Page} has content at all.
	 * 
	 * @return true if has content, false otherwise
	 */
	@XmlElement(name = "hasContent")
	public boolean hasContent() {
		return !content.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return content.iterator();
	}

	/**
	 * @param expression
	 * @param message
	 */
	private void checkArguments(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + page;
		result = prime * result + pageSize;
		result = prime * result + (int) (totalRecords ^ (totalRecords >>> 32));
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Page other = (Page) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		}
		else if (!content.equals(other.content))
			return false;
		if (page != other.page)
			return false;
		if (pageSize != other.pageSize)
			return false;
		if (totalRecords != other.totalRecords)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Page [page=").append(page).append(", pageSize=").append(pageSize).append(", totalRecords=")
				.append(totalRecords).append(", content=").append(content).append("]");
		return builder.toString();
	}

}

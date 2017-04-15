package net.aircommunity.platform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.Comment;

/**
 * Repository interface for {@link Comment} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface CommentRepository extends JpaRepository<Comment, String> {

	/**
	 * Find by comments by product.
	 * 
	 * @param productId the productId
	 * @param pageable the page request
	 * @return page of comment
	 */
	Page<Comment> findByProductId(String productId, Pageable pageable);

	long deleteByProductId(String productId);

}

package net.aircommunity.platform.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.aircommunity.platform.model.domain.Comment;
import net.aircommunity.platform.model.domain.Comment.Source;

/**
 * Repository interface for {@link Comment} instances. Provides basic CRUD operations due to the extension of
 * {@link JpaRepository}.
 * 
 * @author Bin.Zhang
 */
public interface CommentRepository extends JpaRepository<Comment, String> {

	/**
	 * Find by user comment on a product.
	 * 
	 * @param accountId the accountId
	 * @param productId the productId
	 * @return comment
	 */
	Optional<Comment> findByOwnerIdAndProductId(String accountId, String productId);

	/**
	 * Find by comments by product.
	 * 
	 * @param productId the productId
	 * @param pageable the page request
	 * @return page of comment
	 */
	Page<Comment> findByProductIdOrderByDateDesc(String productId, Pageable pageable);

	/**
	 * Find by comments by product.
	 * 
	 * @param productId the productId
	 * @param source the source
	 * @param pageable the page request
	 * @return page of comment
	 */
	Page<Comment> findByProductIdAndSourceOrderByDateDesc(String productId, Source source, Pageable pageable);

	long deleteByProductId(String productId);

}

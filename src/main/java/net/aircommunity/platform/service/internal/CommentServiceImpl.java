package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.Comment;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.repository.CommentRepository;
import net.aircommunity.platform.repository.ProductRepository;
import net.aircommunity.platform.service.CommentService;

/**
 * Comment service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class CommentServiceImpl extends AbstractServiceSupport implements CommentService {
	private static final String CACHE_NAME = "cache.comment";

	@Resource
	private CommentRepository commentRepository;

	// TODO make it cached (productId, product) ?
	@Resource
	private ProductRepository productRepository;

	@Override
	public boolean canComment(String accountId, String productId) {
		Optional<Comment> comment = commentRepository.findByOwnerIdAndProductId(accountId, productId);
		return false;
	}

	@Override
	public Comment createComment(String accountId, String productId, Comment comment) {
		Account owner = findAccount(accountId, Account.class);
		Product product = findProduct(productId);
		// create new
		Comment newComment = new Comment();
		newComment.setDate(new Date());
		newComment.setContent(comment.getContent());
		newComment.setRate(comment.getRate());
		// set vendor
		newComment.setOwner(owner);
		newComment.setProduct(product);
		return commentRepository.save(newComment);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Comment findComment(String commentId) {
		Comment comment = commentRepository.findOne(commentId);
		if (comment == null) {
			throw new AirException(Codes.COMMENT_NOT_FOUND, String.format("Comment %s is not found", commentId));
		}
		return comment;
	}

	private Product findProduct(String productId) {
		Product product = productRepository.findOne(productId);
		if (product == null) {
			throw new AirException(Codes.PRODUCT_NOT_FOUND, String.format("Product %s is not found", productId));
		}
		return product;
	}

	@Override
	public Page<Comment> listComments(String productId, int page, int pageSize) {
		return Pages.adapt(commentRepository.findByProductId(productId, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#commentId")
	@Override
	public void deleteComment(String commentId) {
		commentRepository.delete(commentId);
	}

	@CacheEvict(cacheNames = CACHE_NAME)
	@Override
	public void deleteComments(String productId) {
		commentRepository.deleteByProductId(productId);
	}

}

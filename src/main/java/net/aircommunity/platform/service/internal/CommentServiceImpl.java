package net.aircommunity.platform.service.internal;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.Comment;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.repository.CommentRepository;
import net.aircommunity.platform.repository.OrderRepository;
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

	@Resource
	private OrderRepository orderRepository;

	@Override
	public boolean isCommentAllowed(String accountId, String orderId) {
		Order order = findOrder(orderId);
		boolean isOrderOwner = accountId.equals(order.getOwner().getId());
		return !order.getCommented() && isOrderOwner;
	}

	@Override
	public Comment createComment(String accountId, String orderId, Comment comment) {
		Account owner = findAccount(accountId, Account.class);
		Order order = findOrder(orderId);
		boolean isOrderOwner = accountId.equals(order.getOwner().getId());
		if (order.getCommented() || !isOrderOwner) {
			throw new AirException(Codes.COMMENT_NOT_ALLOWED, String.format(
					"Comment on order %s is not allowed, already commented or your are not order owner", orderId));
		}
		Product product = order.getProduct();
		if (product == null) {
			throw new AirException(Codes.PRODUCT_NOT_FOUND,
					String.format("Comment failed, product of order %s not found", orderId));
		}
		// create new
		Comment newComment = new Comment();
		newComment.setDate(new Date());
		newComment.setContent(comment.getContent());
		newComment.setRate(comment.getRate());
		newComment.setOwner(owner);
		newComment.setProduct(product);
		Comment savedComment = commentRepository.save(newComment);
		double score = product.getScore();
		if (savedComment.getRate() > 0) {
			product.setScore((score + savedComment.getRate()) / 2.0);
		}
		order.setCommented(true);
		orderRepository.save(order);
		productRepository.save(product);
		return savedComment;
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

	private Order findOrder(String orderId) {
		Order order = orderRepository.findOne(orderId);
		if (order == null) {
			throw new AirException(Codes.ORDER_NOT_FOUND, String.format("Order %s is not found", orderId));
		}
		return order;
	}

	// private Product findProduct(String productId) {
	// Product product = productRepository.findOne(productId);
	// if (product == null) {
	// throw new AirException(Codes.PRODUCT_NOT_FOUND, String.format("Product %s is not found", productId));
	// }
	// return product;
	// }

	@Override
	public Page<Comment> listComments(String productId, int page, int pageSize) {
		return Pages.adapt(
				commentRepository.findByProductIdOrderByDateDesc(productId, Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#commentId")
	@Override
	public void deleteComment(String commentId) {
		commentRepository.delete(commentId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteComments(String productId) {
		commentRepository.deleteByProductId(productId);
	}

}

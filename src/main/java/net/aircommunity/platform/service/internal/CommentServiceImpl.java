package net.aircommunity.platform.service.internal;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micro.common.Maths;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.Account;
import net.aircommunity.platform.model.Comment;
import net.aircommunity.platform.model.Order;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.Product;
import net.aircommunity.platform.nls.M;
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
	private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);

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
			LOG.error("Account {} comment on order {} is not allowed, already commented or your are not order owner",
					accountId, orderId);
			throw new AirException(Codes.COMMENT_NOT_ALLOWED, M.bind(M.COMMENT_NOT_ALLOWED));
		}
		if (order.getStatus() != Order.Status.FINISHED) {
			LOG.error("Account {} comment on order {} is not allowed, order is not FINISHED", accountId, orderId);
			throw new AirException(Codes.COMMENT_NOT_ALLOWED, M.bind(M.COMMENT_NOT_ALLOWED_ORDER_NOT_FINISHED));
		}
		// only can be null if CharterOrder
		Product product = order.getProduct();
		if (product == null) {
			LOG.error("Comment failed, product of order {}not found", orderId);
			throw new AirException(Codes.PRODUCT_NOT_FOUND, M.bind(M.PRODUCT_NOT_FOUND));
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
			double productScore = product.getScore();
			if (productScore > 0) {
				score = (score + savedComment.getRate()) / 2.0;
				product.setScore(Maths.round(score, 2, BigDecimal.ROUND_HALF_UP));
			}
			else {
				product.setScore(savedComment.getRate());
			}
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
			LOG.warn("Comment {} not found", commentId);
			throw new AirException(Codes.COMMENT_NOT_FOUND, M.bind(M.COMMENT_NOT_FOUND));
		}
		return comment;
	}

	private Order findOrder(String orderId) {
		Order order = orderRepository.findOne(orderId);
		if (order == null) {
			LOG.warn("Order {} not found", orderId);
			throw new AirException(Codes.ORDER_NOT_FOUND, M.bind(M.ORDER_NOT_FOUND));
		}
		return order;
	}

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

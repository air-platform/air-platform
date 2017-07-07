package net.aircommunity.platform.service.internal.product;

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
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.Comment;
import net.aircommunity.platform.model.domain.Comment.Source;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.Product;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.CommentRepository;
import net.aircommunity.platform.service.internal.AbstractServiceSupport;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.CommonOrderService;
import net.aircommunity.platform.service.product.CommentService;
import net.aircommunity.platform.service.product.CommonProductService;

/**
 * Comment service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class CommentServiceImpl extends AbstractServiceSupport implements CommentService {
	private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);

	private static final String CACHE_NAME = "cache.comment";

	@Resource
	private CommentRepository commentRepository;

	@Resource
	private CommonProductService commonProductService;

	@Resource
	private CommonOrderService commonOrderService;

	@Override
	public long getTotalCommentsCount() {
		return commentRepository.count();
	}

	@Override
	public boolean isCommentAllowed(String accountId, String orderId) {
		Order order = commonOrderService.findOrder(orderId);
		boolean isOrderOwner = accountId.equals(order.getOwner().getId());
		return !order.getCommented() && isOrderOwner;
	}

	@Transactional
	@Override
	public Comment createComment(String accountId, Source source, String sourceId, Comment comment) {
		if (source == null) {
			LOG.error("Account [{}] cannot create comment [{}] with sourceId [{}], because source is null", accountId,
					comment, sourceId);
			throw new AirException(Codes.COMMENT_INVALID_DATA, M.msg(M.COMMENT_INVALID_SOURCE));
		}
		Account owner = findAccount(accountId, Account.class);
		Product product = null;
		Order order = null;
		switch (source) {
		case USER:
			// create comment from product
			product = commonProductService.findProduct(sourceId/* productId */);
			break;

		case BUYER:
			String orderId = sourceId;
			order = commonOrderService.findOrder(orderId);
			boolean isOrderOwner = accountId.equals(order.getOwner().getId());
			if (order.getCommented() || !isOrderOwner) {
				LOG.error(
						"Account {} comment on order {} is not allowed, already commented or your are not order owner",
						accountId, orderId);
				throw new AirException(Codes.COMMENT_NOT_ALLOWED, M.msg(M.COMMENT_NOT_ALLOWED));
			}
			if (order.getStatus() != Order.Status.FINISHED) {
				LOG.error(
						"Account {} comment on order {} is not allowed, order is not FINISHED, current order status: {}",
						accountId, orderId, order.getStatus());
				throw new AirException(Codes.COMMENT_NOT_ALLOWED, M.msg(M.COMMENT_NOT_ALLOWED_ORDER_NOT_FINISHED));
			}
			// only can be null if CharterOrder
			product = order.getProduct();
			if (product == null) {
				LOG.error("Comment failed, product of order {} not found", orderId);
				throw new AirException(Codes.PRODUCT_NOT_FOUND, M.msg(M.PRODUCT_NOT_FOUND));
			}
			break;

		default: // never happen
			throw new AirException(Codes.INTERNAL_ERROR, M.msg(M.INTERNAL_SERVER_ERROR));
		}

		// create comment
		Comment newComment = new Comment();
		newComment.setDate(new Date());
		newComment.setContent(comment.getContent());
		newComment.setRate(source == Source.BUYER ? comment.getRate() : 0);
		newComment.setOwner(owner);
		newComment.setProduct(product);
		newComment.setSource(source);
		// replyTo is already set from REST before creation
		newComment.setReplyTo(comment.getReplyTo());
		try {
			Comment savedComment = commentRepository.save(newComment);
			// calculate score if comment from order
			if (source == Source.BUYER) {
				if (savedComment.getRate() > 0) {
					double newScore = 0.0d;
					double productScore = product.getScore();
					if (productScore > 0) {
						newScore = (productScore + savedComment.getRate()) / 2.0;
						newScore = Maths.round(newScore, 2, BigDecimal.ROUND_HALF_UP);
					}
					else {
						newScore = savedComment.getRate();
					}
					commonProductService.updateProductScore(product.getId(), newScore);
				}
				commonOrderService.updateOrderCommented(order.getId());
			}
			return savedComment;
		}
		catch (Exception e) {
			LOG.error(String.format("User %s create comment %s with source %s and sourceId %s failed, cause: %s",
					accountId, comment, source, sourceId, e.getMessage()), e);
			throw newInternalException();
		}
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Comment findComment(String commentId) {
		Comment comment = commentRepository.findOne(commentId);
		if (comment == null) {
			LOG.warn("Comment {} not found", commentId);
			throw new AirException(Codes.COMMENT_NOT_FOUND, M.msg(M.COMMENT_NOT_FOUND));
		}
		return comment;
	}

	@Override
	public Page<Comment> listComments(String productId, Source source, int page, int pageSize) {
		if (source == null) {
			return Pages.adapt(
					commentRepository.findByProductIdOrderByDateDesc(productId, createPageRequest(page, pageSize)));
		}
		return Pages.adapt(commentRepository.findByProductIdAndSourceOrderByDateDesc(productId, source,
				createPageRequest(page, pageSize)));
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#commentId")
	@Override
	public void deleteComment(String commentId) {
		safeExecute(() -> commentRepository.delete(commentId), "Delete comment %s failed", commentId);
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteComments(String productId) {
		safeExecute(() -> commentRepository.deleteByProductId(productId), "Delete comments for product %s failed",
				productId);
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteCommentsOfAccount(String accountId) {
		safeExecute(() -> commentRepository.deleteByOwnerId(accountId), "Delete comments for account %s failed",
				accountId);
	}
}

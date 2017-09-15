package net.aircommunity.platform.service.internal.product;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;

import javax.annotation.PostConstruct;
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
import net.aircommunity.platform.model.DomainEvent.DomainType;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Account;
import net.aircommunity.platform.model.domain.Aircraft;
import net.aircommunity.platform.model.domain.AircraftComment;
import net.aircommunity.platform.model.domain.Order;
import net.aircommunity.platform.model.domain.QuickFlightOrder;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AircraftCommentRepository;
import net.aircommunity.platform.service.internal.AbstractServiceSupport;
import net.aircommunity.platform.service.internal.Pages;
import net.aircommunity.platform.service.order.QuickFlightOrderService;
import net.aircommunity.platform.service.product.AircraftCommentService;
import net.aircommunity.platform.service.product.AircraftService;

/**
 * AircraftComment service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class AircraftCommentServiceImpl extends AbstractServiceSupport implements AircraftCommentService {
	private static final Logger LOG = LoggerFactory.getLogger(AircraftCommentServiceImpl.class);

	private static final String CACHE_NAME = "cache.aircraft-comment";
	private static final EnumSet<DomainType> INTERESTED_DOMAINS = EnumSet.of(DomainType.ACCOUNT, DomainType.AIRCRAFT);

	@Resource
	private AircraftService aircraftService;

	@Resource
	private AircraftCommentRepository aircraftCommentRepository;

	@Resource
	private QuickFlightOrderService quickFlightOrderService;

	@PostConstruct
	private void init() {
		registerCacheEvictOnDomainEvent(CACHE_NAME, INTERESTED_DOMAINS);
	}

	@Override
	public long getTotalCommentsCount() {
		return aircraftCommentRepository.count();
	}

	@Transactional
	@Override
	public AircraftComment createComment(String accountId, String orderId, AircraftComment comment) {
		Account owner = findAccount(accountId, Account.class);
		QuickFlightOrder order = quickFlightOrderService.findOrder(orderId);

		// test if order owner
		boolean isOrderOwner = accountId.equals(order.getOwner().getId());
		if (order.getCommented() || !isOrderOwner) {
			LOG.error("Account {} comment on order {} is not allowed, already commented or your are not order owner",
					accountId, orderId);
			throw new AirException(Codes.COMMENT_NOT_ALLOWED, M.msg(M.COMMENT_NOT_ALLOWED));
		}
		if (order.getStatus() != Order.Status.FINISHED) {
			LOG.error("Account {} comment on order {} is not allowed, order is not FINISHED, current order status: {}",
					accountId, orderId, order.getStatus());
			throw new AirException(Codes.COMMENT_NOT_ALLOWED, M.msg(M.COMMENT_NOT_ALLOWED_ORDER_NOT_FINISHED));
		}
		// create comment
		Aircraft aircraft = order.getSelectedCandidate().get().getAircraft();
		AircraftComment newComment = new AircraftComment();
		newComment.setDate(new Date());
		newComment.setContent(comment.getContent());
		newComment.setOwner(owner);
		// for sure the candidate should be selected at this time (order is already finished)
		newComment.setAircraft(aircraft);
		// replyTo is already set from REST before creation
		newComment.setReplyTo(comment.getReplyTo());
		newComment.setRate(comment.getRate());
		try {
			AircraftComment savedComment = aircraftCommentRepository.save(newComment);
			// calculate score if comment from order
			if (savedComment.getRate() > 0) {
				double newScore = 0.0d;
				double aircraftScore = aircraft.getScore();
				if (aircraftScore > 0) {
					newScore = (aircraftScore + savedComment.getRate()) / 2.0;
					newScore = Maths.round(newScore, 2, BigDecimal.ROUND_HALF_UP);
				}
				else {
					newScore = savedComment.getRate();
				}
				aircraftService.updateAircraftScore(aircraft.getId(), newScore);
			}
			quickFlightOrderService.updateOrderCommented(order.getId());
			return savedComment;
		}
		catch (Exception e) {
			LOG.error(String.format("User %s create comment %s on order %s failed, cause: %s", accountId, comment,
					orderId, e.getMessage()), e);
			throw newInternalException();
		}
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AircraftComment findComment(String commentId) {
		AircraftComment comment = aircraftCommentRepository.findOne(commentId);
		if (comment == null) {
			LOG.warn("AircraftComment {} not found", commentId);
			throw new AirException(Codes.COMMENT_NOT_FOUND, M.msg(M.COMMENT_NOT_FOUND));
		}
		return comment;
	}

	@Override
	public Page<AircraftComment> listComments(String aircraftId, int page, int pageSize) {
		return Pages.adapt(aircraftCommentRepository.findByAircraftIdOrderByDateDesc(aircraftId,
				createPageRequest(page, pageSize)));
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, key = "#commentId")
	@Override
	public void deleteComment(String commentId) {
		safeExecute(() -> aircraftCommentRepository.delete(commentId), "Delete comment %s failed", commentId);
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteComments(String aircraftId) {
		safeExecute(() -> aircraftCommentRepository.deleteByAircraftId(aircraftId),
				"Delete comments for aircraft %s failed", aircraftId);
	}

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteCommentsOfAccount(String accountId) {
		safeExecute(() -> aircraftCommentRepository.deleteByOwnerId(accountId), "Delete comments for account %s failed",
				accountId);
	}

}

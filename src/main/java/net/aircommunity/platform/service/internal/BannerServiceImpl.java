package net.aircommunity.platform.service.internal;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.model.LinkType;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Banner;
import net.aircommunity.platform.model.domain.Product.Category;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.BannerRepository;
import net.aircommunity.platform.service.BannerService;

/**
 * Banner service implementation (only available for platform ADMIN).
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional(readOnly = true)
public class BannerServiceImpl extends AbstractServiceSupport implements BannerService {

	private static final String CACHE_NAME = "cache.banner";
	private static final String CACHE_NAME_CATEGORY = "cache.banner-category";

	@Resource
	private BannerRepository bannerRepository;

	@Transactional
	@CacheEvict(cacheNames = CACHE_NAME_CATEGORY, allEntries = true)
	@Override
	public Banner createBanner(Banner banner) {
		Banner newBanner = new Banner();
		copyProperties(banner, newBanner);
		newBanner.setCreationDate(new Date());
		return safeExecute(() -> bannerRepository.save(newBanner), "Create banner %s failed", banner);
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public Banner findBanner(String bannerId) {
		Banner banner = bannerRepository.findOne(bannerId);
		if (banner == null) {
			throw new AirException(Codes.BANNER_NOT_FOUND, M.msg(M.BANNER_NOT_FOUND));
		}
		return banner;
	}

	@Transactional
	@CachePut(cacheNames = CACHE_NAME, key = "#bannerId")
	@CacheEvict(cacheNames = CACHE_NAME_CATEGORY, allEntries = true)
	@Override
	public Banner updateBanner(String bannerId, Banner newBanner) {
		Banner banner = findBanner(bannerId);
		copyProperties(newBanner, banner);
		return safeExecute(() -> bannerRepository.save(banner), "Update banner %s failed", newBanner);
	}

	private void copyProperties(Banner src, Banner tgt) {
		tgt.setCategory(src.getCategory());
		tgt.setImage(src.getImage());
		tgt.setLink(src.getLink());
		tgt.setTitle(src.getTitle());
		LinkType type = src.getLinkType();
		tgt.setLinkType(type);
		if (type == LinkType.CONTENT) {
			tgt.setLinkCategory(Category.NONE);
		}
		else {
			Category linkCategory = src.getLinkCategory();
			if (linkCategory == Category.NONE) {
				throw new AirException(Codes.BANNER_INVALID_LINK_CATEGORY,
						M.msg(M.BANNER_INVALID_LINK_CATEGORY, linkCategory));
			}
			tgt.setLinkCategory(linkCategory);
		}
	}

	@Override
	public Page<Banner> listBanners(Category category, int page, int pageSize) {
		if (category == null) {
			return Pages.adapt(bannerRepository.findAll(createPageRequest(page, pageSize)));
		}
		return Pages.adapt(bannerRepository.findByCategory(category, createPageRequest(1, Integer.MAX_VALUE)));
	}

	@Cacheable(cacheNames = CACHE_NAME_CATEGORY)
	@Override
	public List<Banner> listBanners(Category category) {
		return listBanners(category, 1, Integer.MAX_VALUE).getContent();
	}

	@Transactional
	@Caching(evict = { @CacheEvict(cacheNames = CACHE_NAME, key = "#bannerId"),
			@CacheEvict(cacheNames = CACHE_NAME_CATEGORY, allEntries = true) })
	@Override
	public void deleteBanner(String bannerId) {
		bannerRepository.delete(bannerId);
	}

	@Transactional
	@CacheEvict(cacheNames = { CACHE_NAME, CACHE_NAME_CATEGORY }, allEntries = true)
	@Override
	public void deleteBanners() {
		bannerRepository.deleteAll();
	}

}

package net.aircommunity.platform.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.aircommunity.platform.AirException;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.model.domain.Banner;
import net.aircommunity.platform.model.domain.Product.Category;

/**
 * Banner service (only available for platform ADMIN).
 * 
 * @author Bin.Zhang
 */
public interface BannerService {

	/**
	 * Create a Banner.
	 * 
	 * @param banner the banner to be created
	 * @return banner created
	 */
	@Nonnull
	Banner createBanner(@Nonnull Banner banner);

	/**
	 * Retrieves the specified Banner.
	 * 
	 * @param bannerId the bannerId
	 * @return the Banner found
	 * @throws AirException if not found
	 */
	@Nonnull
	Banner findBanner(@Nonnull String bannerId);

	/**
	 * Update a Banner.
	 * 
	 * @param bannerId the bannerId
	 * @param newBanner the Banner to be updated
	 * @return Banner created
	 */
	@Nonnull
	Banner updateBanner(@Nonnull String bannerId, @Nonnull Banner newBanner);

	/**
	 * List all Banners by pagination.
	 * 
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of Banners or empty
	 */
	@Nonnull
	default Page<Banner> listBanners(int page, int pageSize) {
		return listBanners(null, page, pageSize);
	}

	/**
	 * List all Banners filter by category with pagination. (mainly for ADMIN)
	 * 
	 * @param category the category null for list all
	 * @param page the page number
	 * @param pageSize the pageSize
	 * @return a page of Banners or empty
	 */
	@Nonnull
	Page<Banner> listBanners(@Nullable Category category, int page, int pageSize);

	/**
	 * List all Banners
	 * 
	 * @return a list of Banners or empty
	 */
	@Nonnull
	default List<Banner> listBanners() {
		return listBanners(1, Integer.MAX_VALUE).getContent();
	}

	/**
	 * List all banners filter by category
	 * 
	 * @param category the category if Category.NONE to list main banner
	 * @return the banner found
	 * @throws AirException if not found
	 */
	@Nonnull
	List<Banner> listBanners(@Nonnull Category category);

	/**
	 * Delete a Banner.
	 * 
	 * @param bannerId the bannerId
	 */
	void deleteBanner(@Nonnull String bannerId);

	/**
	 * Delete All Banners.
	 */
	void deleteBanners();

}

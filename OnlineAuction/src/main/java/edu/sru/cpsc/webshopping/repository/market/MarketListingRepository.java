package edu.sru.cpsc.webshopping.repository.market;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.domain.widgets.Widget;

@Repository
public interface MarketListingRepository
		extends CrudRepository<MarketListing, Long>, JpaSpecificationExecutor<MarketListing> {

	List<MarketListing> findBySeller(User user);

	MarketListing findByWidgetSold(Widget widget);

	String findNameById(Long id);

	Page<MarketListing> findAll(Pageable pageable);

	Page<MarketListing> findAllByIsDeletedNotInAndQtyAvailableNotIn(Collection<?> isDeleted, Collection<?> quantityZero,
			Pageable pageable);

	Page<MarketListing> findAllByIsDeletedNotInAndQtyAvailableNotInAndPricePerItemBetweenAndQtyAvailableBetween(
			Collection<?> isDeleted, Collection<?> quantityZero, double startPrice, double endPrice, long startQuantity,
			long endQuantity, Pageable pageable);

	@Query("SELECT DISTINCT wa.value FROM MarketListing listing JOIN listing.widgetSold.widgetAttributes wa WHERE wa.attributeKey = :attributeKey")
	List<String> findDistinctAttributeValues(@Param("attributeKey") String attributeKey);

	List<MarketListing> findByCategoryInAndPageViewsGreaterThan(List<String> categories, int pageViews);

	List<MarketListing> findBySellerId(Long sellerId);

	List<MarketListing> findByCategory(String category);

	Page<MarketListing> findByWidgetSoldName(String name, String description, String category, Pageable pageable);

	@Query("SELECT DISTINCT listing FROM MarketListing listing "
			+ "WHERE (LOWER(listing.widgetSold.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ "OR LOWER(listing.widgetSold.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ "OR LOWER(listing.widgetSold.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) "
			+ "AND listing.isDeleted = false " + "AND listing.qtyAvailable > 0")
	Page<MarketListing> searchMarketListings(@Param("searchTerm") String searchTerm, Pageable pageable);
}

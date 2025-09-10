package edu.sru.cpsc.webshopping.repository.market;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.sru.cpsc.webshopping.domain.market.OfferNotification;

@Repository
public interface OfferNotificationRepository extends CrudRepository<OfferNotification, Long> {
	
    public List<OfferNotification> findAllByMarketListingId(Long marketListingId);
    List<OfferNotification> findByMarketListingIdAndPotentialBuyerUserIdOrderByCreatedOnAsc(Long marketListingId, long potentialBuyerUserId);
    
    @Query("SELECT o FROM OfferNotification o WHERE o.marketListingId = :marketListingId " +
            "AND o.potentialBuyerUserId = :potentialBuyerUserId")
     List<OfferNotification> findByMarketListingIdAndPotentialBuyerUserId(
         @Param("marketListingId") Long marketListingId,
         @Param("potentialBuyerUserId") long potentialBuyerUserId
     );
 }



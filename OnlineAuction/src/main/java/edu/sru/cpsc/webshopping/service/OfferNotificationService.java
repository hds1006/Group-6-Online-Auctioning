package edu.sru.cpsc.webshopping.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.sru.cpsc.webshopping.domain.market.OfferNotification;
import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.repository.market.OfferNotificationRepository;
import edu.sru.cpsc.webshopping.repository.market.MarketListingRepository;
import edu.sru.cpsc.webshopping.domain.user.User;

import java.util.*;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class that manages offer notifications in the web shopping application.
 * Handles the creation, tracking, and management of offers between buyers and sellers.
 * This service provides functionality for managing the entire lifecycle of offers
 * including creation, counter-offers, acceptance/rejection, and expiration.
 *
 * @author Edited by Grant Riley
 * @version 2.0
 * @since 1.0
 */
@Service
public class OfferNotificationService {
    @Autowired
    private OfferNotificationRepository offerNotificationRepository;
    
    @Autowired
    private MarketListingRepository marketListingRepository;
    
    @Autowired
    private UserService userService;
    /**
     * Retrieves active offers for a specific user.
     * Includes offers that are unviewed, recently accepted/rejected (within 24 hours),
     * or pending response.
     *
     * @param userId the ID of the user to get offers for
     * @return List of relevant OfferNotification objects, or empty list if none found
     */
    public List<OfferNotification> getActiveOffers(Long userId) {
        try {
            List<OfferNotification> relevantOffers = StreamSupport
                .stream(offerNotificationRepository.findAll().spliterator(), false)
                .filter(offer -> {
                    boolean isActive = !offer.isExpired() && !offer.isRejected() && !offer.isAccepted();
                    
                    boolean isRecentStatusChange = false;
                    if (offer.isRejected() || offer.isAccepted()) {
                        Date statusChangeDate = offer.isAccepted() ? offer.getAcceptedOn() : offer.getUserOfferWindowStart();
                        if (statusChangeDate != null) {
                            long hoursSinceChange = (new Date().getTime() - statusChangeDate.getTime()) / (60 * 60 * 1000);
                            isRecentStatusChange = hoursSinceChange <= 24;
                        }
                    }
                    
                    return isActive || isRecentStatusChange;
                })
                .collect(Collectors.toList());
                
            if (relevantOffers.isEmpty()) {
                return Collections.emptyList();
            }
            
            Set<Long> listingIds = relevantOffers.stream()
                .map(OfferNotification::getMarketListingId)
                .collect(Collectors.toSet());
                
            Map<Long, MarketListing> listingsMap = StreamSupport
                .stream(marketListingRepository.findAllById(listingIds).spliterator(), false)
                .collect(Collectors.toMap(
                    MarketListing::getId,
                    listing -> listing,
                    (existing, replacement) -> existing
                ));
                
            return relevantOffers.stream()
                .filter(offer -> {
                    MarketListing listing = listingsMap.get(offer.getMarketListingId());
                    if (listing == null) {
                        return false;
                    }
                    
                    boolean isSeller = listing.getSeller().getId() == userId;
                    boolean isBuyer = offer.getPotentialBuyerUserId() == userId;
                    
                    if (isSeller) {
                        if (!offer.isHasCounterOffer() && !offer.isOfferViewed()) {
                            return true;
                        }
                        if (offer.isHasCounterOffer()) {
                            if (offer.isAccepted() && !offer.isSellerAcceptedViewed()) {
                                return true;
                            }
                            if (offer.isRejected() && !offer.isSellerRejectedViewed()) {
                                return true;
                            }
                        }
                    }
                    
                    if (isBuyer) {
                        if (offer.isHasCounterOffer() && !offer.isCounterOfferViewed()) {
                            return true;
                        }
                        if (!offer.isHasCounterOffer()) {
                            if (offer.isAccepted() && !offer.isBuyerAcceptedViewed()) {
                                return true;
                            }
                            if (offer.isRejected() && !offer.isBuyerRejectedViewed()) {
                                return true;
                            }
                        }
                    }
                    
                    return false;
                })
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

  
    
    /**
     * Gets the count of unread offers for a user.
     *
     * @param userId the ID of the user to check
     * @return number of unread offers
     */
    public int getUnreadOfferCount(Long userId) {
        return getActiveOffers(userId).size();
    }
    /**
     * Creates a new offer from a buyer for a listing.
     *
     * @param buyerUsername username of the buyer making the offer
     * @param buyerId ID of the buyer making the offer
     * @param amount amount of the offer
     * @param listingId ID of the market listing being offered on
     * @return the created OfferNotification
     * @throws RuntimeException if offer creation fails
     */
    public OfferNotification createOffer(String buyerUsername, long buyerId, String amount, long listingId) {
        try {
            OfferNotification offer = new OfferNotification(buyerUsername, buyerId, amount, listingId);
            offer.setUserOfferWindowStart(new Date());
            return offerNotificationRepository.save(offer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create offer", e);
        }
    }
    /**
     * Creates a counter-offer for an existing offer.
     *
     * @param originalOffer the original offer being countered
     * @param counterOfferAmount amount of the counter-offer
     * @throws RuntimeException if counter-offer creation fails
     */
    public void createCounterOffer(OfferNotification originalOffer, String counterOfferAmount) {
        try {
            originalOffer.setCounterOffer(counterOfferAmount);
            offerNotificationRepository.save(originalOffer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create counter offer", e);
        }
    }
    /**
     * Marks an offer as viewed by either buyer or seller.
     *
     * @param offerId ID of the offer to mark as viewed
     * @param userId ID of the user viewing the offer
     * @throws RuntimeException if marking offer as viewed fails
     */
    public void markOfferAsViewed(Long offerId, Long userId) {
        try {
            OfferNotification offer = offerNotificationRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
                
            MarketListing listing = marketListingRepository.findById(offer.getMarketListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found"));

            boolean isSeller = listing.getSeller().getId() == userId;
            boolean isBuyer = offer.getPotentialBuyerUserId() == userId;

            if (isSeller) {
                offer.markOfferAsViewed();
            } else if (isBuyer && offer.isHasCounterOffer()) {
                offer.markCounterOfferAsViewed();
            }

            offerNotificationRepository.save(offer);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark offer as viewed", e);
        }
    }
    /**
     * Marks all active offers for a user as viewed.
     *
     * @param userId ID of the user viewing all offers
     * @throws RuntimeException if marking offers as viewed fails
     */
    public void markAllOffersAsViewed(Long userId) {
        try {
            List<OfferNotification> activeOffers = getActiveOffers(userId);
            for (OfferNotification offer : activeOffers) {
                MarketListing listing = marketListingRepository.findById(offer.getMarketListingId())
                    .orElseThrow(() -> new RuntimeException("Listing not found"));
                    
                boolean isSeller = listing.getSeller().getId() == userId;
                
                if (isSeller) {
                    offer.markOfferAsViewed();
                } else {
                    offer.markCounterOfferAsViewed();
                }
                
                offerNotificationRepository.save(offer);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark all offers as viewed", e);
        }
    }
    /**
     * Accepts an offer.
     *
     * @param offerId ID of the offer to accept
     * @throws RuntimeException if offer acceptance fails
     */
    public void acceptOffer(Long offerId) {
        try {
            OfferNotification offer = offerNotificationRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
                
            offer.setAccepted(true);
            offerNotificationRepository.save(offer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to accept offer", e);
        }
    }
    /**
     * Rejects an offer.
     *
     * @param offerId ID of the offer to reject
     * @throws RuntimeException if offer rejection fails
     */
    public void rejectOffer(Long offerId) {
        try {
            OfferNotification offer = offerNotificationRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
                
            offer.setRejected(true);
            offerNotificationRepository.save(offer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reject offer", e);
        }
    }
    /**
     * Gets all offers for a specific market listing.
     *
     * @param listingId ID of the market listing
     * @return List of OfferNotifications for the listing
     */
    public List<OfferNotification> getOffersForListing(Long listingId) {
        try {
            return offerNotificationRepository.findAllByMarketListingId(listingId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    /**
     * Gets offers made by a specific user on a listing.
     *
     * @param userId ID of the user
     * @param listingId ID of the market listing
     * @return List of user's OfferNotifications for the listing
     */
    public List<OfferNotification> getUserOffersForListing(Long userId, Long listingId) {
        try {
            return offerNotificationRepository
                .findByMarketListingIdAndPotentialBuyerUserIdOrderByCreatedOnAsc(listingId, userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    /**
     * Checks if a user can make an offer on a listing.
     * Users are limited to 3 offers per listing and cannot have multiple active offers.
     *
     * @param userId ID of the user
     * @param listingId ID of the market listing
     * @return true if user can make an offer, false otherwise
     */
    public boolean canMakeOffer(Long userId, Long listingId) {
        try {
            List<OfferNotification> userOffers = getUserOffersForListing(userId, listingId);
            
            boolean hasActiveOffer = userOffers.stream()
                .anyMatch(offer -> !offer.isRejected() && !offer.isAccepted() && !offer.isExpired());
            
            if (hasActiveOffer) {
                return false;
            }
            
            long offerCount = userOffers.stream()
                .filter(offer -> !offer.isAccepted())
                .count();
            
            return offerCount < 3;
            
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * Gets the number of remaining offers a user can make on a listing.
     * Users are limited to 3 offers per listing.
     *
     * @param userId ID of the user
     * @param listingId ID of the market listing
     * @return number of remaining offers (0-3)
     */
    public int getRemainingOfferCount(Long userId, Long listingId) {
        try {
            List<OfferNotification> userOffers = getUserOffersForListing(userId, listingId);
            
            long offerCount = userOffers.stream()
                .filter(offer -> !offer.isAccepted())
                .count();
                
            return Math.max(0, 3 - (int)offerCount);
            
        } catch (Exception e) {
            return 0;
        }
    }
    /**
     * Checks if an offer is expired.
     * Offers expire after 24 hours if not accepted.
     *
     * @param offer the OfferNotification to check
     * @return true if offer is expired, false otherwise
     */
    public boolean isOfferExpired(OfferNotification offer) {
        return offer != null && offer.isExpired();
    }
}
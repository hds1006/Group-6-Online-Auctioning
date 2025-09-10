package edu.sru.cpsc.webshopping.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.domain.market.OfferNotification;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.market.MarketListingRepository;
import edu.sru.cpsc.webshopping.repository.market.OfferNotificationRepository;

public class OfferNotificationServiceTest {

    @Mock
    private OfferNotificationRepository offerNotificationRepository;

    @Mock
    private MarketListingRepository marketListingRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OfferNotificationService offerNotificationService;

    private User seller;
    private User buyer;
    private MarketListing listing;
    private OfferNotification offer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        seller = new User();
        seller.setId(1L);

        buyer = new User();
        buyer.setId(2L);

        listing = new MarketListing();
        listing.setId(1L);
        listing.setSeller(seller);

        offer = new OfferNotification();
        offer.setId(1L);
        offer.setMarketListingId(listing.getId());
        offer.setPotentialBuyerUserId(buyer.getId());
        offer.setUserOfferWindowStart(new Date());
        
        // Initialize required fields for active offer
        offer.setAccepted(false);
        offer.setRejected(false);
        offer.setHasCounterOffer(false);
        offer.setOfferViewed(false);
        offer.setBuyerAcceptedViewed(false);
        offer.setBuyerRejectedViewed(false);
        offer.setSellerAcceptedViewed(false);
        offer.setSellerRejectedViewed(false);
        offer.setCounterOfferViewed(false);
    }

    @Test
    void testGetActiveOffers_ForBuyer() {
        // Arrange
        // Set up the offer with accepted status but not viewed by buyer
        offer.setAccepted(true);
        offer.setBuyerAcceptedViewed(false);
        offer.setHasCounterOffer(false);
        
        when(offerNotificationRepository.findAll()).thenReturn(Collections.singletonList(offer));
        when(marketListingRepository.findAllById(any())).thenReturn(Collections.singletonList(listing));

        // Act
        List<OfferNotification> activeOffers = offerNotificationService.getActiveOffers(buyer.getId());

        // Assert
        assertEquals(1, activeOffers.size());
        assertEquals(offer.getId(), activeOffers.get(0).getId());
    }

    @Test
    void testGetActiveOffers_ForSeller() {
        // Arrange
        when(offerNotificationRepository.findAll()).thenReturn(Collections.singletonList(offer));
        when(marketListingRepository.findAllById(any())).thenReturn(Collections.singletonList(listing));

        // Act
        List<OfferNotification> activeOffers = offerNotificationService.getActiveOffers(seller.getId());

        // Assert
        assertEquals(1, activeOffers.size());
        assertEquals(offer.getId(), activeOffers.get(0).getId());
    }

    @Test
    void testGetActiveOffers_NoActiveOffersForRejected() {
        // Arrange
        offer.setRejected(true);
        offer.setBuyerRejectedViewed(true);  // Ensure it's viewed so it won't show up as active
        offer.setSellerRejectedViewed(true);
        when(offerNotificationRepository.findAll()).thenReturn(Collections.singletonList(offer));
        when(marketListingRepository.findAllById(any())).thenReturn(Collections.singletonList(listing));

        // Act
        List<OfferNotification> activeOffers = offerNotificationService.getActiveOffers(buyer.getId());

        // Assert
        assertTrue(activeOffers.isEmpty());
    }

    @Test
    void testCreateOffer() {
        // Arrange
        String amount = "100.00";
        when(offerNotificationRepository.save(any(OfferNotification.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        OfferNotification createdOffer = offerNotificationService.createOffer(
            buyer.getUsername(),
            buyer.getId(),
            amount,
            listing.getId()
        );

        // Assert
        assertNotNull(createdOffer);
        assertEquals(buyer.getId(), createdOffer.getPotentialBuyerUserId());
        assertEquals(listing.getId(), createdOffer.getMarketListingId());
        assertEquals(amount, createdOffer.getOfferAmount());
        verify(offerNotificationRepository).save(any(OfferNotification.class));
    }

    @Test
    void testCreateCounterOffer() {
        // Arrange
        String counterOfferAmount = "90.00";
        when(offerNotificationRepository.save(any(OfferNotification.class))).thenReturn(offer);

        // Act
        offerNotificationService.createCounterOffer(offer, counterOfferAmount);

        // Assert
        assertTrue(offer.isHasCounterOffer());
        assertEquals(counterOfferAmount, offer.getCounterOfferAmount());
        verify(offerNotificationRepository).save(offer);
    }

    @Test
    void testMarkOfferAsViewed_BySeller() {
        // Arrange
        when(offerNotificationRepository.findById(offer.getId())).thenReturn(Optional.of(offer));
        when(marketListingRepository.findById(listing.getId())).thenReturn(Optional.of(listing));
        when(offerNotificationRepository.save(any(OfferNotification.class))).thenReturn(offer);

        // Act
        offerNotificationService.markOfferAsViewed(offer.getId(), seller.getId());

        // Assert
        assertTrue(offer.isOfferViewed());
        verify(offerNotificationRepository).save(offer);
    }

    @Test
    void testAcceptOffer() {
        // Arrange
        when(offerNotificationRepository.findById(offer.getId())).thenReturn(Optional.of(offer));
        when(offerNotificationRepository.save(any(OfferNotification.class))).thenReturn(offer);

        // Act
        offerNotificationService.acceptOffer(offer.getId());

        // Assert
        assertTrue(offer.isAccepted());
        verify(offerNotificationRepository).save(offer);
    }

    @Test
    void testRejectOffer() {
        // Arrange
        when(offerNotificationRepository.findById(offer.getId())).thenReturn(Optional.of(offer));
        when(offerNotificationRepository.save(any(OfferNotification.class))).thenReturn(offer);

        // Act
        offerNotificationService.rejectOffer(offer.getId());

        // Assert
        assertTrue(offer.isRejected());
        verify(offerNotificationRepository).save(offer);
    }

    @Test
    void testCanMakeOffer_WithNoExistingOffers() {
        // Arrange
        when(offerNotificationRepository
            .findByMarketListingIdAndPotentialBuyerUserIdOrderByCreatedOnAsc(listing.getId(), buyer.getId()))
            .thenReturn(Collections.emptyList());

        // Act
        boolean canMakeOffer = offerNotificationService.canMakeOffer(buyer.getId(), listing.getId());

        // Assert
        assertTrue(canMakeOffer);
    }

    @Test
    void testCanMakeOffer_WithMaximumOffers() {
        // Arrange
        List<OfferNotification> existingOffers = Arrays.asList(
            createRejectedOffer(),
            createRejectedOffer(),
            createRejectedOffer()
        );
        
        when(offerNotificationRepository
            .findByMarketListingIdAndPotentialBuyerUserIdOrderByCreatedOnAsc(listing.getId(), buyer.getId()))
            .thenReturn(existingOffers);

        // Act
        boolean canMakeOffer = offerNotificationService.canMakeOffer(buyer.getId(), listing.getId());

        // Assert
        assertFalse(canMakeOffer);
    }

    @Test
    void testGetRemainingOfferCount() {
        // Arrange
        List<OfferNotification> existingOffers = Arrays.asList(
            createRejectedOffer(),
            createRejectedOffer()
        );
        
        when(offerNotificationRepository
            .findByMarketListingIdAndPotentialBuyerUserIdOrderByCreatedOnAsc(listing.getId(), buyer.getId()))
            .thenReturn(existingOffers);

        // Act
        int remainingCount = offerNotificationService.getRemainingOfferCount(buyer.getId(), listing.getId());

        // Assert
        assertEquals(1, remainingCount);
    }

    private OfferNotification createRejectedOffer() {
        OfferNotification rejectedOffer = new OfferNotification();
        rejectedOffer.setRejected(true);
        return rejectedOffer;
    }
}
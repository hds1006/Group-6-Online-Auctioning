package edu.sru.cpsc.webshopping.domain.market;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OfferNotificationTest {
    private OfferNotification offer;

    @BeforeEach
    void setUp() {
        offer = new OfferNotification("buyerUser", 1L, "500", 2L);
    }

    @Test
    void testConstructor() {
        assertNotNull(offer.getCreatedOn(), "CreatedOn should be initialized");
        assertEquals("buyerUser", offer.getPotentialBuyerUserName(), "Username should match constructor argument");
        assertEquals(1L, offer.getPotentialBuyerUserId(), "User ID should match constructor argument");
        assertEquals("500", offer.getOfferAmount(), "Offer amount should match constructor argument");
        assertEquals(2L, offer.getMarketListingId(), "Market listing ID should match constructor argument");
        assertFalse(offer.isOfferViewed(), "Offer should not be initially viewed");
        assertFalse(offer.isCounterOfferViewed(), "Counter offer should not be initially viewed");
    }

    @Test
    void testAcceptOffer() {
        offer.setAccepted(true);
        
        assertTrue(offer.isAccepted(), "Offer should be marked as accepted");
        assertNotNull(offer.getAcceptedOn(), "AcceptedOn should be set after acceptance");
        assertFalse(offer.isSellerAcceptedViewed(), "Seller accepted viewed should be false");
        assertFalse(offer.isBuyerAcceptedViewed(), "Buyer accepted viewed should be false");
    }

    @Test
    void testRejectOffer() {
        offer.setRejected(true);
        
        assertTrue(offer.isRejected(), "Offer should be marked as rejected");
        assertFalse(offer.isSellerRejectedViewed(), "Seller rejected viewed should be false");
        assertFalse(offer.isBuyerRejectedViewed(), "Buyer rejected viewed should be false");
    }

    @Test
    void testCounterOffer() {
        offer.setCounterOffer("600");
        
        assertTrue(offer.isHasCounterOffer(), "Should have counter offer");
        assertEquals("600", offer.getCounterOfferAmount(), "Counter offer amount should match");
        assertNotNull(offer.getCounterOfferDate(), "Counter offer date should be set");
        assertTrue(offer.isOfferViewed(), "Original offer should be marked as viewed");
        assertFalse(offer.isCounterOfferViewed(), "Counter offer should not be viewed");
    }

    @Test
    void testTimeWindow() {
        assertFalse(offer.isExpired(), "New offer should not be expired");
        assertTrue(offer.getRemainingTime() > 0, "Should have remaining time");
        assertNotNull(offer.getFormattedRemainingTime(), "Should return formatted time");
        assertFalse(offer.getFormattedRemainingTime().equals("Expired"), "New offer should not show as expired");
    }

    @Test
    void testPaymentWindow() {
        offer.setAccepted(true);
        offer.startPaymentWindow();
        
        assertFalse(offer.isPaymentExpired(), "New payment window should not be expired");
        assertTrue(offer.getRemainingPaymentTime() > 0, "Should have remaining payment time");
        assertNotNull(offer.getFormattedRemainingPaymentTime(), "Should return formatted payment time");
    }

    @Test
    void testNotificationStatuses() {
        // Test initial notification states
        assertTrue(offer.hasUnviewedNotifications(true), "Seller should see unviewed regular offer");
        assertFalse(offer.hasUnviewedNotifications(false), "Buyer should not see notifications initially");

        // Test counter offer notifications
        offer.setCounterOffer("600");
        assertFalse(offer.hasUnviewedNotifications(true), "Seller should not see notifications after counter-offering");
        assertTrue(offer.hasUnviewedNotifications(false), "Buyer should see unviewed counter offer");

        // Test viewed status updates
        offer.markOfferAsViewed();
        offer.markCounterOfferAsViewed();
        assertFalse(offer.hasUnviewedNotifications(true), "No notifications after marking as viewed");
        assertFalse(offer.hasUnviewedNotifications(false), "No notifications after marking as viewed");
    }

    @Test
    void testAcceptanceNotifications() {
        // Test regular offer acceptance
        offer.setAccepted(true);
        assertFalse(offer.shouldShowAcceptance(true), "Seller should not see regular offer acceptance");
        assertTrue(offer.shouldShowAcceptance(false), "Buyer should see regular offer acceptance");

        // Test counter offer acceptance
        OfferNotification counterOffer = new OfferNotification("buyerUser", 1L, "500", 2L);
        counterOffer.setCounterOffer("600");
        counterOffer.setAccepted(true);
        assertTrue(counterOffer.shouldShowAcceptance(true), "Seller should see counter offer acceptance");
        assertFalse(counterOffer.shouldShowAcceptance(false), "Buyer should not see counter offer acceptance");
    }

    @Test
    void testRejectionNotifications() {
        // Test regular offer rejection
        offer.setRejected(true);
        assertFalse(offer.shouldShowRejection(true), "Seller should not see regular offer rejection");
        assertTrue(offer.shouldShowRejection(false), "Buyer should see regular offer rejection");

        // Test counter offer rejection
        OfferNotification counterOffer = new OfferNotification("buyerUser", 1L, "500", 2L);
        counterOffer.setCounterOffer("600");
        counterOffer.setRejected(true);
        assertTrue(counterOffer.shouldShowRejection(true), "Seller should see counter offer rejection");
        assertFalse(counterOffer.shouldShowRejection(false), "Buyer should not see counter offer rejection");
    }

    @Test
    void testResetTimeWindow() {
        // Set a specific initial time
        Date initialTime = new Date(System.currentTimeMillis() - 3600000); // 1 hour ago
        offer.setUserOfferWindowStart(initialTime);
        
        offer.resetTimeWindow();
        
        assertTrue(offer.getUserOfferWindowStart().after(initialTime), "Time window should be reset to a later time");
        assertTrue(offer.isRejected(), "Offer should be marked as rejected after reset");
        assertFalse(offer.isBuyerRejectedViewed(), "Buyer rejected viewed should be reset");
        assertFalse(offer.isSellerRejectedViewed(), "Seller rejected viewed should be reset");
    }

    @Test
    void testOfferActive() {
        assertTrue(offer.isActive(), "New offer should be active");
        
        offer.setAccepted(true);
        assertFalse(offer.isActive(), "Accepted offer should not be active");
        
        OfferNotification rejectedOffer = new OfferNotification("buyerUser", 1L, "500", 2L);
        rejectedOffer.setRejected(true);
        assertFalse(rejectedOffer.isActive(), "Rejected offer should not be active");
    }

    @Test
    void testMarkingViewedStatuses() {
        offer.markOfferAsViewed();
        assertTrue(offer.isOfferViewed(), "Offer should be marked as viewed");

        offer.markCounterOfferAsViewed();
        assertTrue(offer.isCounterOfferViewed(), "Counter offer should be marked as viewed");

        offer.markAcceptedOfferAsViewed(true);
        assertTrue(offer.isSellerAcceptedViewed(), "Seller accepted should be marked as viewed");

        offer.markAcceptedOfferAsViewed(false);
        assertTrue(offer.isBuyerAcceptedViewed(), "Buyer accepted should be marked as viewed");

        offer.markRejectedOfferAsViewed(true);
        assertTrue(offer.isSellerRejectedViewed(), "Seller rejected should be marked as viewed");

        offer.markRejectedOfferAsViewed(false);
        assertTrue(offer.isBuyerRejectedViewed(), "Buyer rejected should be marked as viewed");
    }
}
package edu.sru.cpsc.webshopping.domain.market;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class OfferNotification {
    private static final Logger log = LoggerFactory.getLogger(OfferNotification.class);
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String potentialBuyerUserName;
    private long potentialBuyerUserId;
    
    private String offerAmount;
    private Long marketListingId;
    private Date paymentWindowStart;
    private Date createdOn = new Date();
    private Date acceptedOn = null;
    
    private boolean rejected = false;
    private boolean accepted = false;
    private boolean offerViewed = false;  // Viewed status for original offer
    private boolean counterOfferViewed = false;  // Viewed status for counter offer
    private boolean sellerAcceptedViewed = false; // Seller's viewed status for accepted offers
    private boolean buyerAcceptedViewed = false;  // Buyer's viewed status for accepted offers
    private boolean sellerRejectedViewed = false; // Seller's viewed status for rejected offers
    private boolean buyerRejectedViewed = false;  // Buyer's viewed status for rejected offers
    
    // Counter-offer fields
    private String counterOfferAmount;
    private Date counterOfferDate;
    private boolean hasCounterOffer = false;
    private Date expirationDate;
    
    // Individual user time window
    private Date userOfferWindowStart;
    
    public OfferNotification() {}
    
    public OfferNotification(String potentialBuyerUserName, long potentialBuyerUserId, String offerAmount, long marketListingId) {
        this.potentialBuyerUserName = potentialBuyerUserName;
        this.potentialBuyerUserId = potentialBuyerUserId;
        this.offerAmount = offerAmount;
        this.marketListingId = marketListingId;
        this.userOfferWindowStart = new Date();
        this.offerViewed = false;
        this.counterOfferViewed = false;
        this.sellerAcceptedViewed = false;
        this.buyerAcceptedViewed = false;
        this.sellerRejectedViewed = false;
        this.buyerRejectedViewed = false;
    }
    
    public boolean isExpired() {
        if (userOfferWindowStart == null) {
            return false;
        }
        Date now = new Date();
        Date windowEnd = new Date(userOfferWindowStart.getTime() + 24 * 60 * 60 * 1000);
        return now.after(windowEnd);
    }
    
    public void setCounterOffer(String amount) {
        this.counterOfferAmount = amount;
        this.counterOfferDate = new Date();
        this.hasCounterOffer = true;
        this.offerViewed = true; // Original offer is viewed when counter-offering
        this.counterOfferViewed = false; // New counter offer is unviewed
    }
    
    // Reset time window when counter offer is rejected
    public void resetTimeWindow() {
        this.userOfferWindowStart = new Date();
        this.rejected = true;
        // Reset viewed statuses for rejection
        this.buyerRejectedViewed = false;
        this.sellerRejectedViewed = false;
    }
    
    // Get remaining time in milliseconds
    public long getRemainingTime() {
        if (userOfferWindowStart == null) {
            return 0;
        }
        Date now = new Date();
        Date windowEnd = new Date(userOfferWindowStart.getTime() + 24 * 60 * 60 * 1000);
        return Math.max(0, windowEnd.getTime() - now.getTime());
    }
    
    // Format remaining time as string (HH:mm:ss)
    public String getFormattedRemainingTime() {
        long remaining = getRemainingTime();
        if (remaining <= 0) {
            return "Expired";
        }
        
        long hours = remaining / (60 * 60 * 1000);
        long minutes = (remaining % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (remaining % (60 * 1000)) / 1000;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public void startPaymentWindow() {
        this.paymentWindowStart = new Date();
    }

    public boolean isPaymentExpired() {
        if (paymentWindowStart == null || !accepted) {
            return false;
        }
        Date now = new Date();
        Date windowEnd = new Date(paymentWindowStart.getTime() + 24 * 60 * 60 * 1000);
        return now.after(windowEnd);
    }

    public long getRemainingPaymentTime() {
        if (paymentWindowStart == null || !accepted) {
            return 0;
        }
        Date now = new Date();
        Date windowEnd = new Date(paymentWindowStart.getTime() + 24 * 60 * 60 * 1000);
        return Math.max(0, windowEnd.getTime() - now.getTime());
    }

    public String getFormattedRemainingPaymentTime() {
        long remaining = getRemainingPaymentTime();
        if (remaining <= 0) {
            return "Expired";
        }
        
        long hours = remaining / (60 * 60 * 1000);
        long minutes = (remaining % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (remaining % (60 * 1000)) / 1000;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    // Check if offer is active (not rejected, not accepted, not expired)
    public boolean isActive() {
        return !rejected && !accepted && !isExpired();
    }
    
    // Get window end date
    public Date getWindowEndDate() {
        if (userOfferWindowStart == null) {
            return null;
        }
        return new Date(userOfferWindowStart.getTime() + 24 * 60 * 60 * 1000);
    }

    // Method to check if any notification should be shown
    public boolean hasUnviewedNotifications(boolean isSeller) {
        // For seller
        if (isSeller) {
            // Show regular offers that aren't viewed (only if not counter-offer)
            if (!hasCounterOffer && !offerViewed) return true;
            // Show accepted/rejected notifications for counter offers only (seller sees counter-offer responses)
            if (hasCounterOffer) {
                if (accepted && !sellerAcceptedViewed) return true;
                if (rejected && !sellerRejectedViewed) return true;
            }
        } 
        // For buyer
        else {
            // Show counter offers that aren't viewed
            if (hasCounterOffer && !counterOfferViewed) return true;
            // Show accepted/rejected notifications for regular offers only (buyer sees regular offer responses)
            if (!hasCounterOffer) {
                if (accepted && !buyerAcceptedViewed) return true;
                if (rejected && !buyerRejectedViewed) return true;
            }
        }
        
        return false;
    }

    // Method to handle offer viewed status
    public void markOfferAsViewed() {
        log.debug("Marking offer {} as viewed", id);
        this.offerViewed = true;
    }

    // Method to handle counter offer viewed status
    public void markCounterOfferAsViewed() {
        log.debug("Marking counter-offer {} as viewed", id);
        this.counterOfferViewed = true;
    }

    // Method to handle accepted offer viewed status
    public void markAcceptedOfferAsViewed(boolean isSeller) {
        log.debug("Marking accepted offer {} as viewed for {}", id, isSeller ? "seller" : "buyer");
        if (isSeller) {
            this.sellerAcceptedViewed = true;
        } else {
            this.buyerAcceptedViewed = true;
        }
    }

    // Method to handle rejected offer viewed status
    public void markRejectedOfferAsViewed(boolean isSeller) {
        log.debug("Marking rejected offer {} as viewed for {}", id, isSeller ? "seller" : "buyer");
        if (isSeller) {
            this.sellerRejectedViewed = true;
        } else {
            this.buyerRejectedViewed = true;
        }
    }

    // Helper methods to check status visibility
    public boolean shouldShowAcceptance(boolean isSeller) {
        if (isSeller) {
            // Seller only sees acceptances for counter offers
            return hasCounterOffer && accepted && !sellerAcceptedViewed;
        } else {
            // Buyer only sees acceptances for regular offers
            return !hasCounterOffer && accepted && !buyerAcceptedViewed;
        }
    }

    public boolean shouldShowRejection(boolean isSeller) {
        if (isSeller) {
            // Seller only sees rejections for counter offers
            return hasCounterOffer && rejected && !sellerRejectedViewed;
        } else {
            // Buyer only sees rejections for regular offers
            return !hasCounterOffer && rejected && !buyerRejectedViewed;
        }
    }

    // Method to handle acceptance
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
        if (accepted) {
            log.debug("Setting offer {} as accepted", id);
            this.acceptedOn = new Date();
            this.sellerAcceptedViewed = false;
            this.buyerAcceptedViewed = false;
        }
    }

    // Method to handle rejection
    public void setRejected(boolean rejected) {
        this.rejected = rejected;
        if (rejected) {
            log.debug("Setting offer {} as rejected", id);
            this.sellerRejectedViewed = false;
            this.buyerRejectedViewed = false;
        }
    }
}
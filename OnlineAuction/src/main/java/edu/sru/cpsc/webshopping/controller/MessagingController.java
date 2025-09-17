package edu.sru.cpsc.webshopping.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.domain.market.MarketListingStatus;
import edu.sru.cpsc.webshopping.domain.market.OfferNotification;
import edu.sru.cpsc.webshopping.domain.misc.SocialMessage;
import edu.sru.cpsc.webshopping.domain.user.Statistics;
import edu.sru.cpsc.webshopping.domain.user.Statistics.StatsCategory;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.market.MarketListingRepository;
import edu.sru.cpsc.webshopping.repository.market.OfferNotificationRepository;
import edu.sru.cpsc.webshopping.repository.misc.MessageSocialRepository;
import edu.sru.cpsc.webshopping.service.MessageService;
import edu.sru.cpsc.webshopping.service.NotificationService;
import edu.sru.cpsc.webshopping.service.UserService;

import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller class responsible for handling all messaging and notification functionality
 * in the web shopping application. This includes handling offers, counter-offers,
 * buyer-seller communication, and automated notifications.
 *
 * @author Edited by Grant Riley
 * @version 2.0
 * @since 1.0
 */
@Controller
public class MessagingController {
    Logger log = LoggerFactory.getLogger(MessagingController.class);
    
    private MessageService messageService;
    private MessageSocialRepository messageSocialRepository;
    private NotificationService notificationService;
    private StatisticsDomainController statControl;
    private UserService userService;
    private MarketListingRepository marketListingRepository;
    private OfferNotificationRepository notificationRepository;
    private User internalMessaging;
    
    @Autowired
    public MessagingController(MessageService messageService, UserService userService, NotificationService notificationService, 
            StatisticsDomainController statControl, MessageSocialRepository messageSocialRepository, 
            MarketListingRepository marketListingRepository, OfferNotificationRepository notificationRepository) {
        this.messageService = messageService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.statControl = statControl;
        this.messageSocialRepository = messageSocialRepository;
        this.marketListingRepository = marketListingRepository;
        this.notificationRepository = notificationRepository;
    }
    /**
     * Checks if a user is allowed to send an offer for a market listing.
     * Users are limited to 3 offers per listing and cannot have multiple active offers.
     *
     * @param buyer the User attempting to make an offer
     * @param listing the MarketListing being offered on
     * @return true if the user can send an offer, false otherwise
     */
    private boolean canSendOffer(User buyer, MarketListing listing) {
        List<OfferNotification> userOffers = notificationRepository
            .findByMarketListingIdAndPotentialBuyerUserId(listing.getId(), buyer.getId());
        
        // Check for any active offers
        boolean hasActiveOffer = userOffers.stream()
            .anyMatch(offer -> !offer.isRejected() && !offer.isAccepted());
        
        if (hasActiveOffer) {
            log.info("User {} has active offer on listing {}", buyer.getId(), listing.getId());
            return false;
        }
        
        // Count only non-accepted offers
        long offerCount = userOffers.stream()
            .filter(offer -> !offer.isAccepted())
            .count();
        
        log.info("User {} has made {} offers on listing {}", buyer.getId(), offerCount, listing.getId());
        return offerCount < 3;
    }
    /**
     * Calculates the number of remaining offers a buyer can make on a specific listing.
     * Users are limited to 3 total offers per listing.
     *
     * @param buyer the User making offers
     * @param listing the MarketListing being offered on
     * @return the number of remaining offers allowed (0-3)
     */
    private int getRemainingOffers(User buyer, MarketListing listing) {
        List<OfferNotification> userOffers = notificationRepository
            .findByMarketListingIdAndPotentialBuyerUserId(listing.getId(), buyer.getId());
        
        // Count non-accepted offers
        long offerCount = userOffers.stream()
            .filter(offer -> !offer.isAccepted())
            .count();
            
        int remaining = Math.max(0, 3 - (int)offerCount);
        log.info("User {} has {} offers remaining on listing {}", buyer.getId(), remaining, listing.getId());
        return remaining;
    }
    /**
     * Handles a buyer's counter-offer to a seller's counter-offer.
     * Creates a new offer notification and marks the old one as rejected.
     *
     * @param id the ID of the original offer
     * @param counterOfferAmount the amount of the counter-offer
     * @param principal the authenticated user making the counter-offer
     * @param redirectAttributes for adding flash attributes to the redirect
     * @return redirects to the market listing page
     */
    @PostMapping("/sendBuyerCounterOffer/{id}")
    public String sendBuyerCounterOffer(@PathVariable long id, 
                                      @RequestParam String counterOfferAmount, 
                                      Principal principal, 
                                      RedirectAttributes redirectAttributes) {
        try {
            OfferNotification existingOffer = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
            
            User buyer = userService.getUserByUsername(principal.getName());
            MarketListing listing = marketListingRepository.findById(existingOffer.getMarketListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found"));
            
            // Verify this is the buyer and there's a seller counter offer
            if (existingOffer.getPotentialBuyerUserId() != buyer.getId() || !existingOffer.isHasCounterOffer()) {
                throw new RuntimeException("Unauthorized to send counter offer");
            }

            // Create new offer based on existing one
            OfferNotification newOffer = new OfferNotification(
                buyer.getUsername(),
                buyer.getId(),
                counterOfferAmount,
                existingOffer.getMarketListingId()
            );
            
            // Keep the same offer window start time
            newOffer.setUserOfferWindowStart(existingOffer.getUserOfferWindowStart());
            
            // Save the new offer
            notificationRepository.save(newOffer);
            
            // Mark the old offer as rejected
            existingOffer.setRejected(true);
            notificationRepository.save(existingOffer);

            // Notify seller of counter offer
            SocialMessage message = new SocialMessage();
            message.setSender(userService.getUserByUsername("CustomerService"));
            message.setReceiver(listing.getSeller());
            message.setContent(buyer.getUsername() + " has sent a counter-offer of $" + counterOfferAmount + 
                             " for the listing " + listing.getWidgetSold().getName());
            messageService.saveMessage(message);

            redirectAttributes.addFlashAttribute("offerSentSuccess", true);
            return "redirect:/viewMarketListing/" + existingOffer.getMarketListingId();
            
        } catch (Exception e) {
            log.error("Error in sendBuyerCounterOffer: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing your counter-offer.");
            return "redirect:/viewMarketListing/" + id;
        }
    }
    /**
     * Handles sending messages between buyers and sellers, including both general messages
     * and offer messages. Creates appropriate notifications and tracks statistics.
     *
     * @param id the recipient user ID
     * @param listingId the market listing ID
     * @param content optional message content
     * @param offerAmount optional offer amount
     * @param principal the authenticated user sending the message
     * @param redirectAttributes for adding flash attributes to the redirect
     * @return redirects to the market listing page
     */
    @RequestMapping(value = "/messageSeller", method = RequestMethod.POST)
    public String sendMessage(
            @RequestParam long id, 
            @RequestParam long listingId, 
            @RequestParam(required = false) String content, 
            @RequestParam(required = false) String offerAmount, 
            Principal principal, 
            RedirectAttributes redirectAttributes
    ) {
        try {
            User potentialBuyer = userService.getUserByUsername(principal.getName());
            MarketListing listing = marketListingRepository.findById(listingId)
                    .orElseThrow(() -> new RuntimeException("Listing not found"));
            String listingName = listing.getWidgetSold().getName();

            SocialMessage message = new SocialMessage();

            if (offerAmount != null) {
                int remainingOffers = getRemainingOffers(potentialBuyer, listing);
                
                if (remainingOffers <= 0) {
                    redirectAttributes.addFlashAttribute("error", "You have used all 3 offers allowed for this listing.");
                    return "redirect:/viewMarketListing/" + listingId;
                }

                if (canSendOffer(potentialBuyer, listing)) {
                    OfferNotification offer = new OfferNotification(principal.getName(), potentialBuyer.getId(), offerAmount, listingId);
                    offer.setUserOfferWindowStart(new Date());
                    notificationRepository.save(offer);

                    message.setContent(String.format(
                        "%s has sent you an offer of $%s for the listing %s. " +
                        "You can view your offers on the listing page. This offer window will expire on %s. " +
                        "User has %d offer(s) remaining for this listing.", 
                        principal.getName(),
                        offerAmount,
                        listingName,
                        new Date(offer.getUserOfferWindowStart().getTime() + 24 * 60 * 60 * 1000).toString(),
                        remainingOffers - 1
                    ));

                    String statsDesc = String.format("User %s made offer of $%s on listing %d", 
                        principal.getName(), offerAmount, listingId);
                    if (statsDesc.length() > 255) {
                        statsDesc = statsDesc.substring(0, 252) + "...";
                    }
                    
                    Statistics offerStats = new Statistics(StatsCategory.LISTINGDELETED, 1);
                    offerStats.setDescription(statsDesc);
                    statControl.addStatistics(offerStats);
                    
                    redirectAttributes.addFlashAttribute("offerSentSuccess", true);
                    redirectAttributes.addFlashAttribute("remainingOffers", remainingOffers - 1);
                } else {
                    redirectAttributes.addFlashAttribute("error", "You have an active offer pending for this listing.");
                    return "redirect:/viewMarketListing/" + listingId;
                }
            }
            
            if (content != null) {
                message.setContent("Message received about listing " + listingName + ". " + content);
                
                String statsDesc = String.format("Message from %s to %d about listing %d", 
                    principal.getName(), id, listingId);
                if (statsDesc.length() > 255) {
                    statsDesc = statsDesc.substring(0, 252) + "...";
                }
                
                Statistics messageStats = new Statistics(StatsCategory.MESSAGES, 1);
                messageStats.setDescription(statsDesc);
                statControl.addStatistics(messageStats);
                
                redirectAttributes.addFlashAttribute("messageSentSuccess", true);
            }
            
            if (message.getContent() == null) {
                redirectAttributes.addFlashAttribute("failedToSendOffer", true);
                return "redirect:/viewMarketListing/" + listingId;
            }
            
            User user = userService.getUserByUsername(principal.getName());
            message.setSender(user);
            message.setReceiver(userService.getUserById(id));
            messageService.saveMessage(message);

            return "redirect:/viewMarketListing/" + listingId;
            
        } catch (Exception e) {
            log.error("Error in sendMessage: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "An error occurred while processing your request.");
            return "redirect:/viewMarketListing/" + listingId;
        }
    }
    /**
     * Creates and sends a notification to a buyer when a seller makes a counter-offer.
     *
     * @param offer the OfferNotification containing the counter-offer
     * @param itemName the name of the item being offered on
     */
    public void notifyCounterOfferCreated(OfferNotification offer, String itemName) {
        User buyer = userService.getUserById(offer.getPotentialBuyerUserId());
        
        SocialMessage message = new SocialMessage();
        message.setSender(userService.getUserByUsername("CustomerService"));
        message.setReceiver(buyer);
        
        String messageBody = "A counter-offer of $" + offer.getCounterOfferAmount() + " has been made for your offer on " + itemName + 
            ". You can view and respond to this counter-offer on the listing page.";
        message.setContent(messageBody);
        
        messageService.saveMessage(message);
    }
    /**
     * Creates and sends a notification to a seller when a buyer rejects their counter-offer.
     *
     * @param offer the OfferNotification that was rejected
     * @param itemName the name of the item being offered on
     */
    public void notifyCounterOfferRejected(OfferNotification offer, String itemName) {
        MarketListing listing = marketListingRepository.findById(offer.getMarketListingId())
            .orElseThrow(() -> new RuntimeException("Listing not found"));
        
        User seller = userService.getUserById(listing.getSeller().getId());
        
        SocialMessage message = new SocialMessage();
        message.setSender(userService.getUserByUsername("CustomerService"));
        message.setReceiver(seller);
        
        String messageBody = "Your counter-offer of $" + offer.getCounterOfferAmount() + " for " + itemName + 
            " has been rejected by the buyer. You may make a new counter-offer if you wish.";
        message.setContent(messageBody);
        
        messageService.saveMessage(message);
    }
    /**
     * Creates and sends a notification to a buyer when their offer is accepted.
     * Handles both regular offers and counter-offers.
     *
     * @param offer the OfferNotification that was accepted
     * @param itemName the name of the item being offered on
     */


    public void notifyOfferAccepted(OfferNotification offer, String itemName) {
        User notifiedUser = userService.getUserById(offer.getPotentialBuyerUserId());
        String amount = offer.isHasCounterOffer() ? offer.getCounterOfferAmount() : offer.getOfferAmount();
        String offerType = offer.isHasCounterOffer() ? "counter-offer" : "offer";
        
        SocialMessage message = new SocialMessage();
        message.setSender(userService.getUserByUsername("CustomerService"));
        message.setReceiver(notifiedUser);
        
        String messageBody = "Your " + offerType + " for " + itemName + 
            " for the amount of $" + amount + " has been accepted! Navigate to the page listing to proceed with the transaction.";
        message.setContent(messageBody);
        
        messageService.saveMessage(message);
    }
    /**
     * Creates and sends a notification to a buyer when their offer is rejected.
     *
     * @param offer the OfferNotification that was rejected
     * @param itemName the name of the item being offered on
     */
    public void notifyOfferRejection(OfferNotification offer, String itemName) {
        User potentialBuyer = userService.getUserById(offer.getPotentialBuyerUserId());
        
        SocialMessage message = new SocialMessage();
        message.setSender(userService.getUserByUsername("CustomerService"));
        message.setReceiver(potentialBuyer);
        
        String messageBody = "Your offer for " + itemName + " for the amount of $" + offer.getOfferAmount() + 
            " has been rejected. This is an automated message please do not respond.";
        message.setContent(messageBody);
        
        messageService.saveMessage(message);
    }
    
    /**
     * Scheduled task that runs every minute to check for expired offers.
     * Offers expire 24 hours after they are created if not accepted or rejected.
     * Marks expired offers as rejected and sends notifications.
     */
    @Scheduled(fixedRate = 60000)
    public void checkOfferExpirations() {
        Date now = new Date();
        List<OfferNotification> activeOffers = StreamSupport.stream(notificationRepository.findAll().spliterator(), false)
            .filter(offer -> !offer.isRejected() && !offer.isAccepted())
            .collect(Collectors.toList());

        for (OfferNotification offer : activeOffers) {
            if (offer.getUserOfferWindowStart() != null) {
                Date windowEnd = new Date(offer.getUserOfferWindowStart().getTime() + 24 * 60 * 60 * 1000);
                if (now.after(windowEnd)) {
                    offer.setRejected(true);
                    notificationRepository.save(offer);
                    notifyOfferExpired(offer);
                }
            }
        }
    }
    
	// Fetches the conversation between the user and another user 

	@RequestMapping(value = "/messages/conversation/{otherUserId}", method = RequestMethod.GET) 
	@ResponseBody 
	public List<SocialMessage> getConversation(@PathVariable long otherUserId, Principal principal) {
	 User currentUser = userService.getUserByUsername(principal.getName());
	 User otherUser = userService.getUserById(otherUserId); 

	 // Retrieves the messages 

	 List<SocialMessage> messages = messageService.getAllMessagesForUser(currentUser, otherUser); 

	 // Mark all unread messages for currentUser as read 

	 messageService.markAllMessagesAsRead(currentUser); 

	 return messages; 

	} 
    
    
    // Retrieves all messages to the user
    
 	@RequestMapping(value = "/messages", method = RequestMethod.GET)
 	@ResponseBody
 	public List<SocialMessage> getAllMessages(Principal principal) {
 		User currentUser = userService.getUserByUsername(principal.getName());
 		return messageService.getAllMessagesForUser(currentUser);
 	}

    private void notifyOfferExpired(OfferNotification offer) {
        User buyer = userService.getUserById(offer.getPotentialBuyerUserId());
        MarketListing listing = marketListingRepository.findById(offer.getMarketListingId()).orElseThrow();

        SocialMessage message = new SocialMessage();
        message.setSender(userService.getUserByUsername("CustomerService"));
        message.setReceiver(buyer);
        
        String messageBody = "Your offer for " + listing.getWidgetSold().getName() + 
            " has expired. The 24-hour window for completing the transaction has passed.";
        message.setContent(messageBody);
        
        messageService.saveMessage(message);
    }
}
package edu.sru.cpsc.webshopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.sru.cpsc.webshopping.domain.market.OfferNotification;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.service.OfferNotificationService;
import edu.sru.cpsc.webshopping.service.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
/**
 * Global Controller Advice class that provides common functionality and data
 * across all controllers in the web shopping application.
 * Handles notification counts and active offers that need to be available
 * throughout the application.
 *
 * @author Edited by Grant Riley
 * @version 2.0
 * @since 1.0
 */
@ControllerAdvice
public class GlobalControllerAdvice {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OfferNotificationService offerNotificationService;
    /**
     * Provides a list of active offers for the currently authenticated user.
     * This method automatically adds the active offers to the model for all
     * controllers in the application.
     *
     * @param principal the currently authenticated user's principal
     * @return List of active OfferNotification objects, or empty list if user is not authenticated
     */

    @ModelAttribute("activeOffers")
    public List<OfferNotification> getActiveOffers(Principal principal) {
        if (principal == null) {
            return new ArrayList<>();
        }
        User user = userService.getUserByUsername(principal.getName());
        if (user == null) {
            return new ArrayList<>();
        }
        
        return offerNotificationService.getActiveOffers(user.getId());
    }
    /**
     * REST endpoint that returns the count of unread notifications for the
     * currently authenticated user.
     *
     * @param principal the currently authenticated user's principal
     * @return integer count of unread notifications, returns 0 if user is not authenticated
     */
    @GetMapping("/api/notifications/count")
    @ResponseBody
    public int getNotificationCount(Principal principal) {
        if (principal == null) return 0;
        User user = userService.getUserByUsername(principal.getName());
        if (user == null) return 0;
        return offerNotificationService.getUnreadOfferCount(user.getId());
    }
}
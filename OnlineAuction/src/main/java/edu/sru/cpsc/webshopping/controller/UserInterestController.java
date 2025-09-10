/**
 * This file contains the UserInterestController class, which handles user interests and recommended products.
 * It provides methods to display and submit user preferences, as well as retrieve and display recommended products
 * based on the user's interests and optional price filters.
 *
 * @author Jayden Williams
 * @version 1.0
 * @since 10-31-2024
 */
package edu.sru.cpsc.webshopping.controller;

import java.math.BigDecimal;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.domain.user.UserInterests;
import edu.sru.cpsc.webshopping.repository.market.MarketListingRepository;
import edu.sru.cpsc.webshopping.repository.user.UserRepository;
import edu.sru.cpsc.webshopping.service.MarketListingService;
import edu.sru.cpsc.webshopping.service.UserInterestService;

/**
 * Controller class for handling user interests and recommended products.
 */
@Controller
public class UserInterestController {

	private static final Logger log = LoggerFactory.getLogger(UserInterestController.class);

	@Autowired
	private UserInterestService userInterestService;

	@Autowired
	private MarketListingService marketListingService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MarketListingRepository marketListingRepository;

	/**
     * Displays the user's preference details.
     * 
     * @param model    the model object
     * @param principal the authenticated user principal
     */
	 @GetMapping("/preferenceDetails")
	    public String showPreferenceDetails(Model model, Principal principal) {
	        log.info("Showing preference details for user: {}", principal.getName());
	        User user = userRepository.findByUsername(principal.getName());
	        UserInterests userInterests = userInterestService.findByUserId(user.getId());


		// Creates a new instance if userInterests is null
	        if (userInterests == null) {
	            log.info("Creating new user interests for user: {}", user.getId());
	            userInterests = new UserInterests();
	            userInterests.setHome(false);
	            userInterests.setAuto(false);
	            userInterests.setClothing(false);
	            userInterests.setSports(false);
	            userInterests.setArt(false);
	            userInterests.setCosmetics(false);
	        }
	        
	        model.addAttribute("user", user);
	        model.addAttribute("userInterests", userInterests);
	        return "preferenceDetails";
	    }

	 /**
     * Submits the user's interests.
     * 
     * @param userInterests       the user interests object
     * @param principal           the authenticated user principal
     * @param model               the model object
     * @param redirectAttributes  the redirect attributes
     * @return the redirect URL for preference details
     */
	 @PostMapping("/products/userInterests")
	    public String submitUserInterests(@ModelAttribute("userInterests") UserInterests userInterests, Principal principal,
	            Model model, RedirectAttributes redirectAttributes) {
	        log.info("Submitting interests for user: {}", principal.getName());
	        User user = userRepository.findByUsername(principal.getName());
	        UserInterests existingUserInterests = userInterestService.findByUserId(user.getId());

	        if (existingUserInterests != null) {
	            existingUserInterests.setHome(userInterests.getHome());
	            existingUserInterests.setAuto(userInterests.getAuto());
	            existingUserInterests.setClothing(userInterests.getClothing());
	            existingUserInterests.setSports(userInterests.getSports());
	            existingUserInterests.setArt(userInterests.getArt());
	            existingUserInterests.setCosmetics(userInterests.getCosmetics());
	            userInterestService.saveUserInterests(existingUserInterests);
	        } else {
	            userInterests.setUser(user);
	            userInterestService.saveUserInterests(userInterests);
	        }

	        redirectAttributes.addFlashAttribute("successMessage", "Your interests have been saved successfully.");
	        model.addAttribute("user", user);
	        return "redirect:/preferenceDetails";
	    }

	 /**
     * Displays the recommended products based on the user's interests.
     * 
     * @param userId       the user ID
     * @param maxPrice     the maximum price filter (optional)
     * @param removeFilter the flag to remove the price filter (optional)
     * @param model        the model object
     */
	@GetMapping("/products/recommendations")
	public String showRecommendedProducts(@RequestParam("userId") Long userId,
			@RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,  @RequestParam(value = "removeFilter", required = false) String removeFilter, Model model) {
		log.info("Getting recommendations for user: {} with maxPrice: {}", userId, maxPrice);
		// Gets the user's interests
		UserInterests userInterests = userInterestService.findByUserId(userId);
		 if (removeFilter != null) {
		       
		        return "redirect:/products/recommendations(userId=${user.id})";
		    }
		// Handle case if no interests are selected
		 if (userInterests == null || (!userInterests.getHome() && !userInterests.getAuto() && !userInterests.getClothing()
	                && !userInterests.getSports() && !userInterests.getArt() && !userInterests.getCosmetics())) {
	            log.info("No interests found for user: {}", userId);
	            model.addAttribute("errorMessage", "No interests selected. Click the profile dropdown and go to Preferences");
	            User user = userRepository.findById(userId).orElse(null);
	            model.addAttribute("user", user);
	            return "products/recommendedItems";
	        }

		boolean hasPriceFilter = (maxPrice != null);

		// Gets recommended products based on user interests and price filter
		List<MarketListing> recommendedProducts;
		if (hasPriceFilter) {
			recommendedProducts = userInterestService.getRecommendedProductsByPrice(userInterests, maxPrice);
		} else {
			recommendedProducts = userInterestService.getRecommendedProducts(userInterests);
		}

		// Sorts recommended products by page views in descending order
		recommendedProducts.sort(Comparator.comparing(MarketListing::getPageViews).reversed());

		// Groups recommended products by category
		Map<String, List<MarketListing>> productsByCategory = recommendedProducts.stream()
				.collect(Collectors.groupingBy(MarketListing::getCategory));

		List<String> selectedCategories = new ArrayList<>();
		if (userInterests.getAuto())
			selectedCategories.add("Auto");
		if (userInterests.getHome())
			selectedCategories.add("Home");
		if (userInterests.getClothing())
			selectedCategories.add("Clothing");
		if (userInterests.getSports())
			selectedCategories.add("Sports");
		if (userInterests.getArt())
			selectedCategories.add("Art");
		if (userInterests.getCosmetics())
			selectedCategories.add("Cosmetics");

		// Get popular items for the selected categories
		List<MarketListing> popularItems = null;
		if (!hasPriceFilter) {
			popularItems = userInterestService.getPopularItems(selectedCategories, 250);
			model.addAttribute("popularItems", popularItems);
		}
		model.addAttribute("productsByCategory", productsByCategory);
		model.addAttribute("hasPriceFilter", hasPriceFilter);

		if (recommendedProducts.isEmpty()) {
			model.addAttribute("message", "No products match your interests.");
		} else {
			model.addAttribute("products", recommendedProducts);
		}
		model.addAttribute("maxPrice", maxPrice);
		User user = userRepository.findById(userId).orElse(null);
		model.addAttribute("user", user);
		return "products/recommendedItems"; // displays the recommended products
	}

}
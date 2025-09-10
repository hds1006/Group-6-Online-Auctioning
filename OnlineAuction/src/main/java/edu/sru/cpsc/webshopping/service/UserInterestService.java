/**
 * This file contains the UserInterestService class, which provides services for managing user interests
 * and retrieving recommended products based on those interests.
 *
 * The UserInterestService class interacts with the UserInterestRepository and MarketListingRepository
 * to perform operations such as saving user interests, finding user interests by user ID, retrieving
 * recommended products based on user interests and maximum price, and retrieving popular items based on
 * categories and minimum page views.
 *
 * @author Jayden Williams
 * @version 1.0
 * @since 10-31-2024
 */
package edu.sru.cpsc.webshopping.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.domain.user.UserInterests;
import edu.sru.cpsc.webshopping.repository.market.MarketListingRepository;
import edu.sru.cpsc.webshopping.repository.user.UserInterestRepository;

/**
 * Service class for managing user interests and recommended products.
 */
@Service
public class UserInterestService {

	private static final Logger log = LoggerFactory.getLogger(UserInterestService.class);

    @Autowired
    private UserInterestRepository userInterestRepository;

    @Autowired
    private MarketListingRepository marketListingRepository;

    /**
     * Saves or updates user interests.
     *
     * @param userInterests the user interests to save or update
     */
    public void saveUserInterests(UserInterests userInterests) {
        userInterestRepository.save(userInterests);
    }

    /**
     * Finds user interests by user ID.
     *
     * @param userId the user ID
     * @return the user interests associated with the user ID
     */
    public UserInterests findByUserId(Long userId) {
        log.info("Finding user interests for user ID: {}", userId);
        return userInterestRepository.findByUserId(userId);
    }

    /**
     * Retrieves recommended products based on user interests.
     *
     * @param userInterests the user interests
     * @return a list of recommended products
     */
    public List<MarketListing> getRecommendedProducts(UserInterests userInterests) {
        log.info("Getting recommended products for user ID: {}", userInterests.getId());
        List<MarketListing> recommendedProducts = new ArrayList<>();
        List<MarketListing> allMarketListings = (List<MarketListing>) marketListingRepository.findAll();
        
        for (MarketListing marketListing : allMarketListings) {
            if ((userInterests.getHome() != null && userInterests.getHome() && marketListing.getCategory().equalsIgnoreCase("Home")) ||
                (userInterests.getAuto() != null && userInterests.getAuto() && marketListing.getCategory().equalsIgnoreCase("Auto")) ||
                (userInterests.getClothing() != null && userInterests.getClothing() && marketListing.getCategory().equalsIgnoreCase("Clothing")) ||
                (userInterests.getSports() != null && userInterests.getSports() && marketListing.getCategory().equalsIgnoreCase("Sports")) ||
                (userInterests.getArt() != null && userInterests.getArt() && marketListing.getCategory().equalsIgnoreCase("Art")) ||
                (userInterests.getCosmetics() != null && userInterests.getCosmetics() && marketListing.getCategory().equalsIgnoreCase("Cosmetics"))) {
                recommendedProducts.add(marketListing);
            }
        }
        log.info("Found {} recommended products", recommendedProducts.size());
        return recommendedProducts;
    }

    /**
     * Retrieves recommended products based on user interests and a maximum price.
     *
     * @param userInterests the user interests
     * @param maxPrice      the maximum price
     * @return a list of recommended products within the maximum price
     */
    public List<MarketListing> getRecommendedProductsByPrice(UserInterests userInterests, BigDecimal maxPrice) {
        log.info("Getting recommended products with max price: {}", maxPrice);
        List<MarketListing> recommendedProducts = getRecommendedProducts(userInterests);
        return recommendedProducts.stream()
                .filter(marketListing -> marketListing.getPricePerItem().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves popular items based on categories and a minimum number of page views.
     *
     * @param categories    the list of categories
     * @param minPageViews  the minimum number of page views
     * @return a list of popular items
     */
    public List<MarketListing> getPopularItems(List<String> categories, int minPageViews) {
        log.info("Getting popular items for categories: {} with min views: {}", categories, minPageViews);
        return marketListingRepository.findByCategoryInAndPageViewsGreaterThan(categories, minPageViews);
    }
}
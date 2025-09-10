package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.domain.user.UserInterests;
import edu.sru.cpsc.webshopping.repository.market.MarketListingRepository;
import edu.sru.cpsc.webshopping.repository.user.UserInterestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserInterestServiceTest {

    @InjectMocks
    private UserInterestService userInterestService;

    @Mock
    private UserInterestRepository userInterestRepository;

    @Mock
    private MarketListingRepository marketListingRepository;

    private UserInterests userInterests;
    private List<MarketListing> marketListings;

    @BeforeEach
    public void setUp() {
        // Create test user interests
        userInterests = new UserInterests();
        userInterests.setHome(true);
        userInterests.setClothing(true);

        // Create test market listings
        marketListings = new ArrayList<>();
        
        MarketListing homeListing = new MarketListing();
        homeListing.setCategory("Home");
        homeListing.setPricePerItem(BigDecimal.valueOf(50.00));
        homeListing.setPageViews(300);

        MarketListing clothingListing = new MarketListing();
        clothingListing.setCategory("Clothing");
        clothingListing.setPricePerItem(BigDecimal.valueOf(25.00));
        clothingListing.setPageViews(250);

        MarketListing autoListing = new MarketListing();
        autoListing.setCategory("Auto");
        autoListing.setPricePerItem(BigDecimal.valueOf(100.00));
        autoListing.setPageViews(100);

        marketListings.addAll(Arrays.asList(homeListing, clothingListing, autoListing));
    }

    @Test
    public void testSaveUserInterests() {
        UserInterests testInterests = new UserInterests();

        userInterestService.saveUserInterests(testInterests);

        verify(userInterestRepository, times(1)).save(testInterests);
    }

    @Test
    public void testFindByUserId() {
        Long userId = 1L;
        when(userInterestRepository.findByUserId(userId)).thenReturn(userInterests);

        UserInterests foundInterests = userInterestService.findByUserId(userId);

        assertEquals(userInterests, foundInterests);
        verify(userInterestRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testGetRecommendedProducts() {
        when(marketListingRepository.findAll()).thenReturn(marketListings);

        List<MarketListing> recommendedProducts = userInterestService.getRecommendedProducts(userInterests);

        assertEquals(2, recommendedProducts.size());
        assertTrue(recommendedProducts.stream().anyMatch(ml -> ml.getCategory().equals("Home")));
        assertTrue(recommendedProducts.stream().anyMatch(ml -> ml.getCategory().equals("Clothing")));
    }

    @Test
    public void testGetRecommendedProductsByPrice() {
        when(marketListingRepository.findAll()).thenReturn(marketListings);
        BigDecimal maxPrice = BigDecimal.valueOf(75.00);

        List<MarketListing> recommendedProducts = userInterestService.getRecommendedProductsByPrice(userInterests, maxPrice);

        assertEquals(2, recommendedProducts.size());
        assertTrue(recommendedProducts.stream().allMatch(ml -> ml.getPricePerItem().compareTo(maxPrice) <= 0));
    }

    @Test
    public void testGetPopularItems() {
        List<String> categories = Arrays.asList("Home", "Clothing");
        int minPageViews = 200;
        List<MarketListing> popularItems = Arrays.asList(
            marketListings.get(0),
            marketListings.get(1)
        );
        when(marketListingRepository.findByCategoryInAndPageViewsGreaterThan(categories, minPageViews))
            .thenReturn(popularItems);

        List<MarketListing> result = userInterestService.getPopularItems(categories, minPageViews);

        assertEquals(2, result.size());
        verify(marketListingRepository, times(1)).findByCategoryInAndPageViewsGreaterThan(categories, minPageViews);
    }

    @Test
    public void testGetRecommendedProductsWithNoInterests() {
        UserInterests noInterests = new UserInterests();
        when(marketListingRepository.findAll()).thenReturn(marketListings);

        List<MarketListing> recommendedProducts = userInterestService.getRecommendedProducts(noInterests);

        assertEquals(0, recommendedProducts.size());
    }
}

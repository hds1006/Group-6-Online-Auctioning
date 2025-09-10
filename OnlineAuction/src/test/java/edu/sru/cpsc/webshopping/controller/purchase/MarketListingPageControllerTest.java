package edu.sru.cpsc.webshopping.controller.purchase;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import edu.sru.cpsc.webshopping.controller.EmailController;
import edu.sru.cpsc.webshopping.controller.MarketListingDomainController;
import edu.sru.cpsc.webshopping.controller.MessagingController;
import edu.sru.cpsc.webshopping.controller.StatisticsDomainController;
import edu.sru.cpsc.webshopping.controller.TransactionController;
import edu.sru.cpsc.webshopping.controller.UserController;
import edu.sru.cpsc.webshopping.controller.WidgetController;
import edu.sru.cpsc.webshopping.controller.WidgetImageController;
import edu.sru.cpsc.webshopping.controller.billing.SellerRatingController;
import edu.sru.cpsc.webshopping.domain.market.MarketListing;
import edu.sru.cpsc.webshopping.domain.market.MarketListingStatus;
import edu.sru.cpsc.webshopping.domain.market.OfferNotification;
import edu.sru.cpsc.webshopping.domain.market.Transaction;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.domain.widgets.Category;
import edu.sru.cpsc.webshopping.domain.widgets.Widget;
import edu.sru.cpsc.webshopping.domain.widgets.WidgetImage;
import edu.sru.cpsc.webshopping.repository.market.MarketListingRepository;
import edu.sru.cpsc.webshopping.repository.market.OfferNotificationRepository;
import edu.sru.cpsc.webshopping.repository.user.UserRepository;
import edu.sru.cpsc.webshopping.service.CategoryService;
import edu.sru.cpsc.webshopping.service.StatisticsService;
import edu.sru.cpsc.webshopping.service.UserService;
import edu.sru.cpsc.webshopping.service.WatchlistService;

@SpringBootTest(classes = {MarketListingPageControllerTest.class})
public class MarketListingPageControllerTest {

    @Mock private MarketListingDomainController mlDomainController;
    @Mock private UserController userController;
    @Mock private WidgetController widgetController;
    @Mock private TransactionController transController;
    @Mock private PurchaseShippingAddressPageController shippingPage;
    @Mock private EmailController emailController;
    @Mock private WidgetImageController widgetImageController;
    @Mock private SellerRatingController ratingController;
    @Mock private WatchlistService watchlistService;
    @Mock private ConfirmPurchasePageController purchaseController;
    @Mock private Model model;
    @Mock private BindingResult result;
    @Mock private UserService userService;
    @Mock private Principal principal;
    @Mock private MarketListingRepository marketListingRepository;
    @Mock private MessagingController messagingController;
    @Mock private OfferNotificationRepository offerNotificationRepository;
    @Mock private StatisticsService statisticsService;
    @Mock private CategoryService categoryService;
    @Mock private StatisticsDomainController statisticsDomainController;
    @Mock private UserRepository userRepository;

    private MockMvc mockMvc;
    private MarketListingPageController pageController;
    private MarketListing testListing;
    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setWishlistedWidgets(new HashSet<>());
        testUser.setRole("ROLE_USER");
        
        // Initialize test listing
        testListing = createBasicMarketListing();
        
        // Set up mocks
        setupMockBehaviors();
        
        // Initialize controller and inject dependencies
        initializeController();
        
        mockMvc = MockMvcBuilders.standaloneSetup(pageController).build();
    }

    private void setupMockBehaviors() {
        when(principal.getName()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(mlDomainController.getMarketListing(1L)).thenReturn(testListing);
        when(offerNotificationRepository.findAllByMarketListingId(anyLong())).thenReturn(new ArrayList<>());
        when(marketListingRepository.findById(1L)).thenReturn(Optional.of(testListing));
        when(widgetImageController.getwidgetImageByMarketListing(any())).thenReturn(new WidgetImage[0]);
        when(watchlistService.countUsersWithMarketListingInWatchlist(anyLong())).thenReturn(0L);
        when(purchaseController.initializePurchasePage(any(), any(), any(), any())).thenReturn("confirmPurchasePage");
        when(mlDomainController.getListingsBySellerId(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(mlDomainController.getSimilarListings(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(userRepository.findByUsername(anyString())).thenReturn(testUser);
        
        doNothing().when(statisticsDomainController).addStatistics(any());
        doNothing().when(emailController).messageEmail(any(), any(), any());
        doNothing().when(messagingController).notifyCounterOfferCreated(any(), any());
        doNothing().when(messagingController).notifyCounterOfferRejected(any(), any());
        doNothing().when(messagingController).notifyOfferAccepted(any(), any());
        doNothing().when(messagingController).notifyOfferRejection(any(), any());
        doNothing().when(statisticsService).incrementPageView(anyLong());
    }

    private void initializeController() {
        pageController = new MarketListingPageController(
            mlDomainController, 
            transController, 
            userController,
            shippingPage,
            emailController,
            widgetController,
            widgetImageController,
            ratingController,
            null,
            purchaseController,
            watchlistService
        );
        
        try {
            injectDependencies(pageController);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void injectDependencies(MarketListingPageController controller) throws Exception {
        setField(controller, "userService", userService);
        setField(controller, "marketListingRepository", marketListingRepository);
        setField(controller, "offerNotificationRepository", offerNotificationRepository);
        setField(controller, "messagingController", messagingController);
        setField(controller, "statisticsService", statisticsService);
        setField(controller, "categoryService", categoryService);
        setField(controller, "statisticsDomainController", statisticsDomainController);
        setField(controller, "userRepository", userRepository);
    }
    
    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = MarketListingPageController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
    
    private MarketListing createBasicMarketListing() {
        MarketListing listing = new MarketListing();
        listing.setId(1L);
        listing.setDeleted(false);
        listing.setPricePerItem(new BigDecimal("50.05"));
        listing.setQtyAvailable(50);
        listing.setSeller(testUser);
        
        Widget widget = new Widget();
        widget.setCategory(new Category("appliance"));
        widget.setName("Test Widget");
        listing.setWidgetSold(widget);
        
        return listing;
    }

    @Test
    public void viewMarketListingPageBasicTest() {
        String viewName = pageController.viewMarketListingPage(1L, model, principal);
        assertEquals("viewMarketListing", viewName);
        verify(mlDomainController).getMarketListing(1L);
        verify(offerNotificationRepository, atLeastOnce()).findAllByMarketListingId(1L);
    }

    @Test
    public void attemptPurchaseBasicTest() {
        pageController.viewMarketListingPage(1L, model, principal);
        
        Transaction transaction = new Transaction();
        transaction.setQtyBought(20);
        
        when(marketListingRepository.findById(1L)).thenReturn(Optional.of(testListing));
        when(result.hasErrors()).thenReturn(false);
        
        String viewName = pageController.attemptPurchase(transaction, result, model, principal);
        assertNotNull(viewName);
        
        verify(userService, atLeastOnce()).getUserByUsername("testuser");
        verify(purchaseController).initializePurchasePage(any(), any(), any(), any());
    }

    @Test
    public void deletedListingTest() {
        testListing.setDeleted(true);
        when(mlDomainController.getMarketListing(1L)).thenReturn(testListing);
        
        String viewName = pageController.viewMarketListingPage(1L, model, principal);
        assertEquals("redirect:/homePage", viewName);
    }

    @Test
    public void viewMarketListingPageZeroQuantityTest() {
        testListing.setQtyAvailable(0);
        when(mlDomainController.getMarketListing(1L)).thenReturn(testListing);
        
        String viewName = pageController.viewMarketListingPage(1L, model, principal);
        
        assertEquals("redirect:/homePage", viewName);
    }

  

    @Test
    public void acceptOfferTest() {
        OfferNotification offer = new OfferNotification();
        offer.setMarketListingId(1L);
        offer.setOfferAmount("45.00");
        offer.setId(1L);
        
        when(offerNotificationRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(marketListingRepository.findById(1L)).thenReturn(Optional.of(testListing));
        
        String viewName = pageController.acceptOffer(1L, principal);
        
        assertTrue(offer.isAccepted());
        assertEquals(MarketListingStatus.PENDING, testListing.getListingStatus());
        verify(offerNotificationRepository).save(offer);
        verify(marketListingRepository).save(testListing);
        verify(messagingController).notifyOfferAccepted(eq(offer), any());
        assertEquals("redirect:/viewMarketListing/1", viewName);
    }

    @Test
    public void createCounterOfferTest() {
        OfferNotification offer = new OfferNotification();
        offer.setMarketListingId(1L);
        
        when(offerNotificationRepository.findById(1L)).thenReturn(Optional.of(offer));
        
        String viewName = pageController.createCounterOffer(1L, "45.00", principal);
        
        verify(offerNotificationRepository).save(offer);
        verify(messagingController).notifyCounterOfferCreated(eq(offer), any());
        assertEquals("redirect:/viewMarketListing/1", viewName);
    }

    @Test
    public void rejectCounterOfferTest() {
        OfferNotification offer = new OfferNotification();
        offer.setMarketListingId(1L);
        offer.setHasCounterOffer(true);
        offer.setPotentialBuyerUserId(1L);
        
        when(offerNotificationRepository.findById(1L)).thenReturn(Optional.of(offer));
        
        String viewName = pageController.rejectCounterOffer(1L, principal);
        
        assertTrue(offer.isRejected());
        verify(offerNotificationRepository).save(offer);
        verify(messagingController).notifyCounterOfferRejected(eq(offer), any());
        assertEquals("redirect:/viewMarketListing/1", viewName);
    }

    @Test
    public void rejectOfferTest() {
        OfferNotification offer = new OfferNotification();
        offer.setMarketListingId(1L);
        
        when(offerNotificationRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(marketListingRepository.findById(1L)).thenReturn(Optional.of(testListing));
        
        String viewName = pageController.rejectOffer(1L);
        
        assertTrue(offer.isRejected());
        verify(offerNotificationRepository).save(offer);
        verify(messagingController).notifyOfferRejection(eq(offer), any());
        assertEquals("redirect:/viewMarketListing/1", viewName);
    }
    
    @Test
    public void addToWishlistTest() {
        String viewName = pageController.wishlistItem(1L, model, principal);
        assertEquals("redirect:/viewMarketListing/1", viewName);
        verify(userController).addToWishlist(any(MarketListing.class), eq(principal));
    }

    @Test
    public void removeFromWishlistTest() {
        String viewName = pageController.delWishlistItem(1L, model, principal);
        assertEquals("redirect:/viewMarketListing/1", viewName);
        verify(userController).removeFromWishlist(any(MarketListing.class), eq(principal));
    }

 
    @Test
    public void reloadModelTest() {
        pageController.viewMarketListingPage(1L, model, principal);
        pageController.reloadModel(model, testUser);
        
        verify(model, atLeastOnce()).addAttribute("currListing", testListing);
        verify(model, atLeastOnce()).addAttribute("widget", testListing.getWidgetSold());
        verify(model, atLeastOnce()).addAttribute("category", testListing.getWidgetSold().getCategory());
        verify(model, atLeastOnce()).addAttribute("user", testUser);
    }

   

    @Test
    public void viewMarketListingPageWithActiveOffersTest() {
        List<OfferNotification> offers = new ArrayList<>();
        OfferNotification offer = new OfferNotification();
        offers.add(offer);
        
        when(offerNotificationRepository.findAllByMarketListingId(1L)).thenReturn(offers);
        
        String viewName = pageController.viewMarketListingPage(1L, model, principal);
        
        assertEquals("viewMarketListing", viewName);
        verify(model, atLeastOnce()).addAttribute(eq("sellerOffers"), anyList());
    }

    @Test
    public void attemptPurchaseWithValidationErrorTest() {
        pageController.viewMarketListingPage(1L, model, principal);
        
        Transaction transaction = new Transaction();
        transaction.setQtyBought(100); // More than available quantity
        
        when(result.hasErrors()).thenReturn(true);
        
        String viewName = pageController.attemptPurchase( transaction, result, model, principal);
        
        assertEquals("viewMarketListing", viewName);
        verify(model, atLeastOnce()).addAttribute(eq("user"), any(User.class));
    }

    @Test
    public void attemptPurchaseWithAcceptedOfferAndPriceAdjustmentTest() {
        pageController.viewMarketListingPage(1L, model, principal);
        
        OfferNotification acceptedOffer = new OfferNotification();
        acceptedOffer.setOfferAmount("45.00");
        acceptedOffer.setPotentialBuyerUserId(1L);
        acceptedOffer.setId(1L);
        testListing.setAcceptedOfferId(1L);
        
        when(offerNotificationRepository.findById(1L)).thenReturn(Optional.of(acceptedOffer));
        when(result.hasErrors()).thenReturn(false);
        
        Transaction transaction = new Transaction();
        transaction.setQtyBought(1);
        
        String viewName = pageController.attemptPurchase( transaction, result, model, principal);
        
        verify(purchaseController).initializePurchasePage(eq(testListing), any(Transaction.class), eq(model), eq(principal));
        assertNotNull(viewName);
    }

    @Test
    public void returnToListingsTest() {
        String viewName = pageController.returnToListings();
        assertEquals("redirect:/homePage", viewName);
    }

    @Test
    public void viewWishlistTest() {
        String viewName = pageController.getWishlist(principal);
        assertEquals("watchlist", viewName);
        verify(userService).getUserByUsername(anyString());
    }

    @Test
    public void editListingTest() {
        MarketListing editedListing = createBasicMarketListing();
        editedListing.setPricePerItem(new BigDecimal("60.00"));
        
        String viewName = pageController.editListing(editedListing, model, principal);
        
        verify(mlDomainController).editMarketListing(eq(editedListing), any(User.class));
        assertEquals("redirect:/viewMarketListing/" + editedListing.getId(), viewName);
    }

    @Test
    public void deleteListingTest() {
        when(userService.getUserByUsername(principal.getName())).thenReturn(testUser);
        
        String viewName = pageController.deleteListing(1L, model, principal);
        
        verify(mlDomainController).deleteMarketListing(1L);
        assertEquals("redirect:/homePage", viewName);
    }

    @Test
    public void viewMarketListingPageWithStatisticsTest() {
        when(userService.getUserByUsername(principal.getName())).thenReturn(testUser);
        User differentUser = new User();
        differentUser.setId(2L); // Different from seller ID
        testListing.setSeller(differentUser);
        
        String viewName = pageController.viewMarketListingPage(1L, model, principal);
        
        assertEquals("viewMarketListing", viewName);
        verify(statisticsService).incrementPageView(1L);
    }
}
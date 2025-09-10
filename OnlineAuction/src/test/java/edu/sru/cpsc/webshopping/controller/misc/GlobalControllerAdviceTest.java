package edu.sru.cpsc.webshopping.controller.misc;

import edu.sru.cpsc.webshopping.controller.GlobalControllerAdvice;
import edu.sru.cpsc.webshopping.domain.market.OfferNotification;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.service.OfferNotificationService;
import edu.sru.cpsc.webshopping.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalControllerAdviceTest {

    @Mock
    private UserService userService;

    @Mock
    private OfferNotificationService offerNotificationService;

    @Mock
    private Principal principal;

    @InjectMocks
    private GlobalControllerAdvice globalControllerAdvice;

    private User testUser;
    private List<OfferNotification> testOffers;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testOffers = new ArrayList<>();
        OfferNotification offer = new OfferNotification();
        offer.setId(1L);
        testOffers.add(offer);
    }

    @Test
    void getActiveOffers_WhenPrincipalIsNull_ReturnsEmptyList() {
        List<OfferNotification> result = globalControllerAdvice.getActiveOffers(null);
        
        assertTrue(result.isEmpty());
        verifyNoInteractions(userService, offerNotificationService);
    }

    @Test
    void getActiveOffers_WhenUserNotFound_ReturnsEmptyList() {
        when(principal.getName()).thenReturn("nonexistentUser");
        when(userService.getUserByUsername("nonexistentUser")).thenReturn(null);

        List<OfferNotification> result = globalControllerAdvice.getActiveOffers(principal);
        
        assertTrue(result.isEmpty());
        verify(userService).getUserByUsername("nonexistentUser");
        verifyNoInteractions(offerNotificationService);
    }

    @Test
    void getActiveOffers_WhenUserExists_ReturnsOfferList() {
        when(principal.getName()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(testUser);
        when(offerNotificationService.getActiveOffers(testUser.getId())).thenReturn(testOffers);

        List<OfferNotification> result = globalControllerAdvice.getActiveOffers(principal);
        
        assertFalse(result.isEmpty());
        assertEquals(testOffers, result);
        verify(userService).getUserByUsername("testUser");
        verify(offerNotificationService).getActiveOffers(testUser.getId());
    }

    @Test
    void getNotificationCount_WhenPrincipalIsNull_ReturnsZero() {
        int result = globalControllerAdvice.getNotificationCount(null);
        
        assertEquals(0, result);
        verifyNoInteractions(userService, offerNotificationService);
    }

    @Test
    void getNotificationCount_WhenUserNotFound_ReturnsZero() {
        when(principal.getName()).thenReturn("nonexistentUser");
        when(userService.getUserByUsername("nonexistentUser")).thenReturn(null);

        int result = globalControllerAdvice.getNotificationCount(principal);
        
        assertEquals(0, result);
        verify(userService).getUserByUsername("nonexistentUser");
        verifyNoInteractions(offerNotificationService);
    }

    @Test
    void getNotificationCount_WhenUserExists_ReturnsCount() {
        when(principal.getName()).thenReturn("testUser");
        when(userService.getUserByUsername("testUser")).thenReturn(testUser);
        when(offerNotificationService.getUnreadOfferCount(testUser.getId())).thenReturn(5);

        int result = globalControllerAdvice.getNotificationCount(principal);
        
        assertEquals(5, result);
        verify(userService).getUserByUsername("testUser");
        verify(offerNotificationService).getUnreadOfferCount(testUser.getId());
    }
}
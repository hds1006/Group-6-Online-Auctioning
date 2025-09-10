package edu.sru.cpsc.webshopping.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.domain.user.UserInterests;
import edu.sru.cpsc.webshopping.repository.user.UserRepository;
import edu.sru.cpsc.webshopping.service.UserInterestService;

@ExtendWith(MockitoExtension.class)
public class UserInterestControllerTest {

    @InjectMocks
    private UserInterestController controller;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInterestService userInterestService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private RedirectAttributes redirectAttributes;

    private User testUser;
    private UserInterests testInterests;

    @BeforeEach
    public void setUp() {
        //test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setId(1L);

        //test interests
        testInterests = new UserInterests();
        testInterests.setUser(testUser);
        testInterests.setHome(true);
        testInterests.setClothing(true);

        when(principal.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(userInterestService.findByUserId(testUser.getId())).thenReturn(testInterests);
    }

    @Test
    public void testShowPreferenceDetails() {
        String viewName = controller.showPreferenceDetails(model, principal);
        
        assertEquals("preferenceDetails", viewName);
        verify(model).addAttribute("user", testUser);
        verify(model).addAttribute("userInterests", testInterests);
    }

    @Test
    public void testSubmitUserInterests() {
        String viewName = controller.submitUserInterests(testInterests, principal, model, redirectAttributes);
        
        assertEquals("redirect:/preferenceDetails", viewName);
        verify(userInterestService).saveUserInterests(testInterests);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Your interests have been saved successfully.");
    }
}
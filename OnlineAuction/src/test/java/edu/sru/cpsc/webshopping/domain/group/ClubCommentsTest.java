package edu.sru.cpsc.webshopping.domain.group;

import edu.sru.cpsc.webshopping.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ClubCommentsTest {

    @Mock
    private User user;

    @Mock
    private ClubPosts post;

    private ClubComments clubComments;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  
        clubComments = new ClubComments();  
    }

    @Test
    public void testGettersAndSetters() {
        // Test for ID
        clubComments.setId(1L);
        assertEquals(1L, clubComments.getId());

        // Test for Content
        clubComments.setContent("This is a comment.");
        assertEquals("This is a comment.", clubComments.getContent());

        // Test for PostId
        clubComments.setPostId(123L);
        assertEquals(123L, clubComments.getPostId());

        // Test for User
        clubComments.setUser(user);
        assertEquals(user, clubComments.getUser());
    }

    @Test
    public void testDefaultValues() {
        assertNull(clubComments.getId());
        assertNull(clubComments.getContent());
        assertNull(clubComments.getPostId());
        assertNull(clubComments.getUser());
    }

    @Test
    public void testSetContentAndPostId() {
        clubComments.setContent("Test comment");
        clubComments.setPostId(10L);

        assertNotNull(clubComments.getContent());
        assertEquals("Test comment", clubComments.getContent());

        assertEquals(10L, clubComments.getPostId());
    }
}
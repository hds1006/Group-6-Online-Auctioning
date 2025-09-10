package edu.sru.cpsc.webshopping.domain.group;

import edu.sru.cpsc.webshopping.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

public class ClubPostsTest {

    @Mock
    private User user;

    @Mock
    private ClubComments clubComment;

    private ClubPosts clubPosts;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  
        clubPosts = new ClubPosts();  
    }

    @Test
    public void testGettersAndSetters() {
        // Test for ID
        clubPosts.setId(1L);
        assertEquals(1L, clubPosts.getId());

        // Test for Title
        clubPosts.setTitle("Test Post Title");
        assertEquals("Test Post Title", clubPosts.getTitle());

        // Test for Content
        clubPosts.setContent("This is the content of the post.");
        assertEquals("This is the content of the post.", clubPosts.getContent());

        // Test for SubgroupId
        clubPosts.setSubgroupId(456L);
        assertEquals(456L, clubPosts.getSubgroupId());

        // Test for User
        clubPosts.setUser(user);
        assertEquals(user, clubPosts.getUser());
    }

    @Test
    public void testCommentsRelationship() {
        Set<ClubComments> comments = clubPosts.getComments();
        assertNotNull(comments);
        assertTrue(comments.isEmpty());

        clubPosts.getComments().add(clubComment);
        assertFalse(clubPosts.getComments().isEmpty());
        assertTrue(clubPosts.getComments().contains(clubComment));
    }

    @Test
    public void testSetComments() {
        Set<ClubComments> comments = new HashSet<>();
        comments.add(clubComment);
        clubPosts.setComments(comments);
        
        assertEquals(1, clubPosts.getComments().size());
        assertTrue(clubPosts.getComments().contains(clubComment));
    }
    
    @Test
    public void testDefaultValues() {
        assertNull(clubPosts.getId());
        assertNull(clubPosts.getTitle());
        assertNull(clubPosts.getContent());
        assertNull(clubPosts.getSubgroupId());
        assertNull(clubPosts.getUser());
        assertNotNull(clubPosts.getComments()); 
        assertTrue(clubPosts.getComments().isEmpty()); 
    }
}
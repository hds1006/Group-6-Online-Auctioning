package edu.sru.cpsc.webshopping.domain.group;

import edu.sru.cpsc.webshopping.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

public class SubSubGroupPostsTest {

    private SubSubGroupPosts subSubGroupPost;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        subSubGroupPost = new SubSubGroupPosts();
    }

    @Test
    public void testSubSubGroupPostAttributes() {
        subSubGroupPost.setId(1L);
        assertEquals(1L, subSubGroupPost.getId());
        subSubGroupPost.setTitle("Test Post");
        assertEquals("Test Post", subSubGroupPost.getTitle());
        subSubGroupPost.setContent("Test content");
        assertEquals("Test content", subSubGroupPost.getContent());
    }

    @Test
    public void testSubSubGroupPostRelationships() {
        subSubGroupPost.setUser(user);
        assertEquals(user, subSubGroupPost.getUser());
    }

    @Test
    public void testSetAndGetComments() {
        Set<SubComment> comments = new HashSet<>();
        SubComment comment = new SubComment();
        comments.add(comment);
        subSubGroupPost.setComments(comments);
        assertTrue(subSubGroupPost.getComments().contains(comment));
    }

  

 
}
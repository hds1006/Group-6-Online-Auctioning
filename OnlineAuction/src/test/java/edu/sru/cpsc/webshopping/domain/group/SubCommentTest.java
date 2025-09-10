package edu.sru.cpsc.webshopping.domain.group;

import edu.sru.cpsc.webshopping.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubCommentTest {

    private SubComment subComment;
    private User user;
    private SubSubGroupPosts subSubGroupPost;

    @BeforeEach
    public void setUp() {
        subComment = new SubComment();
        user = new User();
        subSubGroupPost = new SubSubGroupPosts();
    }

    @Test
    public void testSubCommentAttributes() {
        subComment.setId(1L);
        assertEquals(1L, subComment.getId());
        subComment.setContent("Test content");
        assertEquals("Test content", subComment.getContent());
    }

    @Test
    public void testSubCommentRelationships() {
        subComment.setSubSubGroupPost(subSubGroupPost);
        assertEquals(subSubGroupPost, subComment.getSubSubGroupPost());
        subComment.setUser(user);
        assertEquals(user, subComment.getUser());
    }
}
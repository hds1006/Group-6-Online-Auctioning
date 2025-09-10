
/**
 * The {@code SubComment} class represents a comment made on a post in a sub-subgroup within the web shopping domain.
 * Each sub-comment contains content, is associated with a specific post, and is made by a user.
 * 
 * <p>This entity models a relationship where each sub-comment is linked to a specific {@link SubSubGroupPosts} entity,
 * and each sub-comment is created by a {@link User}. It is used to facilitate commenting on specific posts within a
 * subgroup's sub-subgroup.</p>
 * 
 * <p>Author: Griffin Vasalani</p>
 */

package edu.sru.cpsc.webshopping.domain.group;

import edu.sru.cpsc.webshopping.domain.user.User;
import jakarta.persistence.*;

@Entity
public class SubComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "subsubgroup_post_id")
    private SubSubGroupPosts subSubGroupPost;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and setters
    
    /**
     * Gets the unique identifier of the sub-comment.
     *
     * @return the ID of the sub-comment.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the sub-comment.
     *
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the content of the sub-comment.
     *
     * @return the content of the sub-comment.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the sub-comment.
     *
     * @param content the content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the {@link SubSubGroupPosts} entity associated with this sub-comment.
     *
     * @return the post associated with the sub-comment.
     */
    public SubSubGroupPosts getSubSubGroupPost() {
        return subSubGroupPost;
    }

    /**
     * Sets the {@link SubSubGroupPosts} entity associated with this sub-comment.
     *
     * @param subSubGroupPost the post to associate with the sub-comment.
     */
    public void setSubSubGroupPost(SubSubGroupPosts subSubGroupPost) {
        this.subSubGroupPost = subSubGroupPost;
    }

    /**
     * Gets the {@link User} who made the sub-comment.
     *
     * @return the user who made the sub-comment.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the {@link User} who made the sub-comment.
     *
     * @param user the user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }
}
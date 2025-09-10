/**
 * The {@code ClubComments} class represents a comment on a club post in the web shopping domain.
 * This class is mapped to a database table where each comment is associated with a user and a specific post.
 * The comments can contain text and are related to a particular post by {@code post_id}.
 * 
 * <p>Each comment is linked to a {@link User} and a {@link ClubPosts} entity, which denotes the 
 * user who made the comment and the post to which the comment belongs.</p>
 * 
 * <p>This entity is used to store user-generated content for posts within a club in the application.</p>
 * 
 * <p>Author: Griffin Vasalani</p>
 */
package edu.sru.cpsc.webshopping.domain.group;  
  
import java.sql.Date;

import edu.sru.cpsc.webshopping.domain.user.User;
import jakarta.persistence.Entity;  
import jakarta.persistence.GeneratedValue;  
import jakarta.persistence.GenerationType;  
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;  
  
@Entity
public class ClubComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Long post_id;
    
    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private ClubPosts post;
    
    // Getters and setters
    /**
     * Gets the unique identifier of the comment.
     *
     * @return the ID of the comment.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the comment.
     *
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the content of the comment.
     *
     * @return the content of the comment.
     */
    
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the comment.
     *
     * @param content the content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * Gets the post ID to which this comment is related.
     *
     * @return the post ID.
     */

    public Long getPostId() {
        return post_id;
    }

    /**
     * Sets the post ID to which this comment is related.
     *
     * @param post_id the post ID to set.
     */
    
    public void setPostId(Long post_id) {
        this.post_id = post_id;
    }

    /**
     * Gets the user who created this comment.
     *
     * @return the user who made the comment.
     */
    public User getUser() {
        return user;
    }
    /**
     * Sets the user who created this comment.
     *
     * @param user the user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }
}
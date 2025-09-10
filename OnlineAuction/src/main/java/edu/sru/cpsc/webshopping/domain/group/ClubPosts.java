/**
 * The {@code ClubPosts} class represents a post within a club in the web shopping domain.
 * Each post has a title, content, and is associated with a specific user and a subgroup.
 * 
 * <p>Posts can have many comments which are stored in a set of {@link ClubComments} entities.
 * A post is linked to a {@link User}, who is the author of the post, and to a {@code subgroupId},
 * which represents the subgroup in which the post belongs.</p>
 * 
 * <p>Each post can have many associated comments, and any changes to a post will cascade to the comments
 * due to the {@link CascadeType.ALL} setting on the comments relationship.</p>
 * 
 * <p>Author: Griffin Vasalani</p>
 */

package edu.sru.cpsc.webshopping.domain.group;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import edu.sru.cpsc.webshopping.domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class ClubPosts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Long subgroupId;
    
    @ManyToOne
    private User user;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClubComments> comments = new HashSet<>();

    // Getters and setters
    /**
     * Gets the unique identifier of the post.
     *
     * @return the ID of the post.
     */
    public Long getId() {
        return id;
    }
    /**
     * Sets the unique identifier of the post.
     *
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the title of the post.
     *
     * @return the title of the post.
     */
    
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title of the post.
     *
     * @param title the title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Gets the content of the post.
     *
     * @return the content of the post.
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Sets the content of the post.
     *
     * @param content the content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * Gets the subgroup ID to which this post belongs.
     *
     * @return the subgroup ID.
     */
    public Long getSubgroupId() {
        return subgroupId;
    }
    
    /**
     * Sets the subgroup ID to which this post belongs.
     *
     * @param subgroupId the subgroup ID to set.
     */
    public void setSubgroupId(Long subgroupId) {
        this.subgroupId = subgroupId;
    }
    
    /**
     * Gets the user who created this post.
     *
     * @return the user who created the post.
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Sets the user who created this post.
     *
     * @param user the user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * Gets the set of comments associated with this post.
     *
     * @return the set of comments.
     */
    public Set<ClubComments> getComments() {
        return comments;
    }
    
    /**
     * Sets the set of comments associated with this post.
     *
     * @param comments the comments to set.
     */
    
    public void setComments(Set<ClubComments> comments) {
        this.comments = comments;
    }
}
/**
 * The {@code SubSubGroupPosts} class represents a post made in a sub-subgroup within a club in the web shopping domain.
 * A post contains a title and content and is associated with a specific sub-subgroup. It also allows for comments
 * to be made by users on the post. This class is mapped to the "sub_sub_group_posts" table in the database.
 * 
 * <p>Each post is created by a user, and each sub-subgroup can have multiple posts associated with it. 
 * The posts can also have multiple comments, represented by the {@link SubComment} class.</p>
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
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class SubSubGroupPosts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Long subsubgroupId;
    
    @ManyToOne
    private User user;
    
    @OneToMany(mappedBy = "subSubGroupPost", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<SubComment> comments = new HashSet<>();

    // Getters and setters
    /**
     * Gets the unique identifier of the post.
     * 
     * @return the ID of the post
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the unique identifier of the post.
     * 
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the title of the post.
     * 
     * @return the title of the post
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title of the post.
     * 
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Gets the content of the post.
     * 
     * @return the content of the post
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Sets the content of the post.
     * 
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * Gets the identifier of the sub-subgroup this post belongs to.
     * 
     * @return the sub-subgroup ID
     */
    public Long getSubSubgroupId() {
        return subsubgroupId;
    }
    
    /**
     * Sets the identifier of the sub-subgroup this post belongs to.
     * 
     * @param subsubgroupId the sub-subgroup ID to set
     */
    public void setSubSubgroupId(Long subsubgroupId) {
        this.subsubgroupId = subsubgroupId;
    }
    
    /**
     * Gets the {@link User} who created this post.
     * 
     * @return the user who created the post
     */
    public User getUser() {
        return user;
    }
    

    /**
     * Sets the {@link User} who created this post.
     * 
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * Gets the set of comments associated with this post.
     * 
     * @return the set of comments
     */
    public Set<SubComment> getComments() {
        return comments;
    }

    /**
     * Sets the set of comments associated with this post.
     * 
     * @param comments the set of comments to set
     */
    public void setComments(Set<SubComment> comments) {
        this.comments = comments;
    }
   
}
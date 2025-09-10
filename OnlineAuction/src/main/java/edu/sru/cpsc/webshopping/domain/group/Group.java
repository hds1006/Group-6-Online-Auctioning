
/**
 * The {@code Group} class represents a user group within the web shopping domain.
 * Each group has a name, description, group image, and a set of users who are members of the group.
 * It also has a set of messages, sent by users within the group, and an owner who is typically the creator of the group.
 * 
 * <p>This entity manages a many-to-many relationship between {@link Group} and {@link User}, 
 * where a group can have multiple members, and a user can be part of multiple groups. 
 * It also handles one-to-many relationships with {@link SocialMessage}, representing messages posted within the group.</p>
 * 
 * <p>Author: Griffin Vasalani</p>
 */
package edu.sru.cpsc.webshopping.domain.group;

import edu.sru.cpsc.webshopping.domain.misc.SocialMessage;
import edu.sru.cpsc.webshopping.domain.user.User; // Ensure this import matches the location of your User entity
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_groups") // Ensure this table name does not conflict with reserved SQL keywords
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    // New field for storing group image path or identifier
    private String groupImage;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "group_user", // Name of the join table
        joinColumns = @JoinColumn(name = "group_id"), // Column for Group ID
        inverseJoinColumns = @JoinColumn(name = "user_id") // Column for User ID
    )
    private Set<User> members = new HashSet<>();
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    private Set<SocialMessage> messages = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

	private String status;


    // Getters and setters for all fields

	/**
     * Gets the unique identifier of the group.
     *
     * @return the ID of the group.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the group.
     *
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    /**
     * Gets the name of the group.
     *
     * @return the name of the group.
     */
    public String getName() {
        return name;
    }

    
    /**
     * Sets the name of the group.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the group.
     *
     * @return the description of the group.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the group.
     *
     * @param description the description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the group image.
     *
     * @return the group image.
     */
    public String getGroupImage() {
        return groupImage;
    }

    /**
     * Sets the group image.
     *
     * @param groupImage the group image to set.
     */
    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    /**
     * Gets the set of members in the group.
     *
     * @return the set of members.
     */
    public Set<User> getMembers() {
        return members;
    }

    /**
     * Sets the set of members in the group.
     *
     * @param members the set of members to set.
     */
    public void setMembers(Set<User> members) {
        this.members = members;
    }
    
    /**
     * Adds a member to the group.
     *
     * @param user the user to add as a member.
     */
    public void addMember(User user) {
        this.members.add(user);
        user.getGroups().add(this); // Ensure bidirectional synchronization
    }

    /**
     * Removes a member from the group.
     *
     * @param user the user to remove from the group.
     */
    public void removeMember(User user) {
        this.members.remove(user);
        user.getGroups().remove(this); // Ensure bidirectional synchronization
    }
    
    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    /**
     * Gets the set of messages posted in the group.
     *
     * @return the set of messages.
     */
    public Set<SocialMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<SocialMessage> messages) {
        this.messages = messages;
    }

    // No-argument constructor is required by JPA
    public Group() {}
    
    /**
     * Gets the owner (creator) of the group.
     *
     * @return the owner of the group.
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the group.
     *
     * @param owner the user to set as the owner.
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Gets the status of the group (e.g., active, archived, etc.).
     *
     * @return the status of the group.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the status of the group.
     *
     * @param status the status to set.
     */
    public String getStatus() {
        return status;
    }

}

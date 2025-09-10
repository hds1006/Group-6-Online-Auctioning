/**
 * The {@code SubGroup} class represents a subgroup within a club in the web shopping domain.
 * A subgroup is a smaller unit within a larger {@link Clubs} entity, and it is associated with a specific {@link User} as its owner.
 * The class provides functionality for managing subgroup membership and subgroups within the subgroup.
 * 
 * <p>This entity is mapped to the "subgroups" table in the database and contains information such as the subgroup name, description, owner, creation date, and related subgroups.
 * Each {@code SubGroup} is linked to one {@link Clubs} and has many members who are {@link User}s.</p>
 * 
 * <p>Author: Griffin Vasalani</p>
 */

package edu.sru.cpsc.webshopping.domain.group;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import edu.sru.cpsc.webshopping.domain.user.User;

@Entity
@Table(name = "subgroups")
public class SubGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @ManyToOne
    @JoinColumn(name = "car_club_id", nullable = false)
    private Clubs clubs;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;
    
    @OneToMany(mappedBy = "subGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubSubGroup> subSubGroups = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "subgroup_members",
        joinColumns = @JoinColumn(name = "subgroup_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
   
    
    private Set<User> members = new HashSet<>();
    
    private String image;

    // Constructors
    public SubGroup() {
    }

    
    /**
     * Constructs a new {@code SubGroup} with the specified parameters.
     * 
     * @param name the name of the subgroup
     * @param description the description of the subgroup
     * @param clubs the {@link Clubs} this subgroup belongs to
     * @param owner the {@link User} who owns this subgroup
     * @param creationDate the creation date of the subgroup
     */
    
    public SubGroup(String name, String description, Clubs clubs, User owner, LocalDateTime creationDate) {
        this.name = name;
        this.description = description;
        this.clubs = clubs;
        this.owner = owner;
        this.creationDate = creationDate;
    }
    
    /**
     * Creates and adds a new {@link SubSubGroup} to this subgroup.
     * 
     * @param name the name of the new sub-subgroup
     * @param description the description of the new sub-subgroup
     * @param currentUser the user who is creating the sub-subgroup
     * @return the newly created {@link SubSubGroup}
     */
    
    public SubSubGroup createSubSubGroup(String name, String description, User currentUser) {
        SubSubGroup newSubSubGroup = new SubSubGroup(
            name, 
            description, 
            this, 
            this.getClubs(), 
            currentUser, 
            LocalDateTime.now()
        );
        this.subSubGroups.add(newSubSubGroup);
        return newSubSubGroup;
    }

    // Getter and setter for subSubGroups
    
    /**
     * Gets the set of {@link SubSubGroup} entities associated with this subgroup.
     * 
     * @return the set of sub-subgroups
     */
    
    public Set<SubSubGroup> getSubSubGroups() {
        return subSubGroups;
    }

    /**
     * Sets the set of {@link SubSubGroup} entities associated with this subgroup.
     * 
     * @param subSubGroups the set of sub-subgroups to set
     */
    public void setSubSubGroups(Set<SubSubGroup> subSubGroups) {
        this.subSubGroups = subSubGroups;
    }
    

    // Getters and Setters
    /**
     * Gets the unique identifier of the subgroup.
     * 
     * @return the ID of the subgroup
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the subgroup.
     * 
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the name of the subgroup.
     * 
     * @return the name of the subgroup
     */


    public String getName() {
        return name;
    }

    
    /**
     * Sets the name of the subgroup.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /**
     * Gets the description of the subgroup.
     * 
     * @return the description of the subgroup
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the subgroup.
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the {@link Clubs} this subgroup belongs to.
     * 
     * @return the {@link Clubs} entity
     */
    public Clubs getClubs() {
        return clubs;
    }
    
    
    /**
     * Sets the {@link Clubs} this subgroup belongs to.
     * 
     * @param clubs the {@link Clubs} entity to set
     */
    public void setClubs(Clubs clubs) {
        this.clubs = clubs;
    }

    /**
     * Gets the {@link User} who owns this subgroup.
     * 
     * @return the owner of the subgroup
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sets the {@link User} who owns this subgroup.
     * 
     * @param owner the owner to set
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Gets the creation date of the subgroup.
     * 
     * @return the creation date
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the subgroup.
     * 
     * @param creationDate the creation date to set
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the set of {@link User}s who are members of this subgroup.
     * 
     * @return the set of members
     */
    public Set<User> getMembers() {
        return members;
    }

    /**
     * Sets the set of {@link User}s who are members of this subgroup.
     * 
     * @param members the set of members to set
     */
    public void setMembers(Set<User> members) {
        this.members = members;
    }

    // Helper methods
    /**
     * Adds a {@link User} to the subgroup's members.
     * 
     * @param user the user to add
     */
    
    public void addMember(User user) {
        this.members.add(user);
    }

    /**
     * Removes a {@link User} from the subgroup's members.
     * 
     * @param user the user to remove
     */
    public void removeMember(User user) {
        this.members.remove(user);
    }

    /**
     * Checks if a user is a member of the subgroup.
     * 
     * @param user the user to check
     * @return {@code true} if the user is a member or the owner of the subgroup, {@code false} otherwise
     */
    public boolean isMember(User user) {
        return this.members.contains(user) || this.owner.equals(user);
    }

    /**
     * Gets the image URL or path associated with the subgroup.
     * 
     * @return the image URL or path
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the image URL or path associated with the subgroup.
     * 
     * @param image the image URL or path to set
     */
    public void setImage(String image) {
        this.image = image;
    }
    
    @Override
    public String toString() {
        return "Subgroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", carClub=" + (clubs != null ? clubs.getId() : null) +
                ", owner=" + (owner != null ? owner.getId() : null) +
                ", creationDate=" + creationDate +
                ", membersCount=" + members.size() +
                '}';
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubGroup)) return false;
        SubGroup subgroup = (SubGroup) o;
        return getId() != null && getId().equals(subgroup.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
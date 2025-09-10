/**
 * The {@code SubSubGroup} class represents a subgroup within a subgroup of a club in the web shopping domain.
 * A sub-subgroup is a smaller unit within a {@link SubGroup} and is part of a {@link Clubs} entity. 
 * It is also associated with a specific {@link User} as its owner. 
 * This entity is mapped to the "sub_sub_groups" table in the database.
 * 
 * <p>The {@code SubSubGroup} allows for the creation of nested subgroups within a main subgroup and provides functionality 
 * for managing members and interactions between subgroups. It is intended to facilitate the creation of more granular, 
 * organized groups under a club structure.</p>
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
@Table(name = "sub_sub_groups")
public class SubSubGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @ManyToOne
    @JoinColumn(name = "subgroup_id", nullable = false)
    private SubGroup subGroup;

    @ManyToOne
    @JoinColumn(name = "car_club_id", nullable = false)
    private Clubs clubs;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sub_sub_group_members",
        joinColumns = @JoinColumn(name = "sub_sub_group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    // Constructors
    public SubSubGroup() {
    }

    /**
     * Constructs a new {@code SubSubGroup} with the specified parameters.
     * 
     * @param name the name of the new sub-subgroup
     * @param description the description of the new sub-subgroup
     * @param subGroup the {@link SubGroup} this sub-subgroup belongs to
     * @param clubs the {@link Clubs} the sub-subgroup is associated with
     * @param owner the {@link User} who owns the sub-subgroup
     * @param creationDate the creation date of the sub-subgroup
     */
    
    public SubSubGroup(String name, String description, SubGroup subGroup, Clubs clubs, User owner, LocalDateTime creationDate) {
        this.name = name;
        this.description = description;
        this.subGroup = subGroup;
        this.clubs = clubs;
        this.owner = owner;
        this.creationDate = creationDate;
    }

    // Getters and Setters
    /**
     * Gets the unique identifier of the sub-subgroup.
     * 
     * @return the ID of the sub-subgroup
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the sub-subgroup.
     * 
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the sub-subgroup.
     * 
     * @return the name of the sub-subgroup
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the sub-subgroup.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the sub-subgroup.
     * 
     * @return the description of the sub-subgroup
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the sub-subgroup.
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the {@link SubGroup} this sub-subgroup belongs to.
     * 
     * @return the {@link SubGroup} entity
     */
    public SubGroup getSubGroup() {
        return subGroup;
    }

    /**
     * Sets the {@link SubGroup} this sub-subgroup belongs to.
     * 
     * @param subGroup the {@link SubGroup} entity to set
     */
    public void setSubGroup(SubGroup subGroup) {
        this.subGroup = subGroup;
    }

    /**
     * Gets the {@link Clubs} this sub-subgroup is associated with.
     * 
     * @return the {@link Clubs} entity
     */
    public Clubs getClubs() {
        return clubs;
    }

    /**
     * Sets the {@link Clubs} this sub-subgroup is associated with.
     * 
     * @param clubs the {@link Clubs} entity to set
     */
    public void setClubs(Clubs clubs) {
        this.clubs = clubs;
    }

    /**
     * Gets the {@link User} who owns the sub-subgroup.
     * 
     * @return the owner of the sub-subgroup
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sets the {@link User} who owns the sub-subgroup.
     * 
     * @param owner the owner to set
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    /**
     * Gets the creation date of the sub-subgroup.
     * 
     * @return the creation date
     */

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the sub-subgroup.
     * 
     * @param creationDate the creation date to set
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the set of {@link User}s who are members of this sub-subgroup.
     * 
     * @return the set of members
     */
    public Set<User> getMembers() {
        return members;
    }

    /**
     * Sets the set of {@link User}s who are members of this sub-subgroup.
     * 
     * @param members the set of members to set
     */
    public void setMembers(Set<User> members) {
        this.members = members;
    }

    // Helper methods
    

    /**
     * Adds a {@link User} to the sub-subgroup's members.
     * 
     * @param user the user to add
     */
    public void addMember(User user) {
        this.members.add(user);
    }
    
    /**
     * Removes a {@link User} from the sub-subgroup's members.
     * 
     * @param user the user to remove
     */

    public void removeMember(User user) {
        this.members.remove(user);
    }

    /**
     * Checks if a {@link User} is a member of the sub-subgroup.
     * 
     * @param user the user to check
     * @return {@code true} if the user is a member or the owner, {@code false} otherwise
     */
    public boolean isMember(User user) {
        return this.members.contains(user) || this.owner.equals(user);
    }

    @Override
    public String toString() {
        return "SubSubGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subGroup=" + (subGroup != null ? subGroup.getId() : null) +
                ", carClub=" + (clubs != null ? clubs.getId() : null) +
                ", owner=" + (owner != null ? owner.getId() : null) +
                ", creationDate=" + creationDate +
                ", membersCount=" + members.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubSubGroup)) return false;
        SubSubGroup subSubGroup = (SubSubGroup) o;
        return getId() != null && getId().equals(subSubGroup.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
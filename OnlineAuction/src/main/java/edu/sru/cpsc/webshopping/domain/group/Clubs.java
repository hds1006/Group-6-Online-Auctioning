/**
 * The {@code Clubs} class represents a club within the web shopping domain.
 * Each club has a name, description, creation date, and an image URL.
 * It also manages a list of members and associated subgroups. The club supports 
 * creating and managing subgroups, adding and removing members, and provides methods 
 * for managing its attributes and relationships.
 * 
 * <p>The club can have many members and subgroups. The members are stored in a 
 * {@link Set} of {@link User}, and each club can have multiple subgroups represented 
 * by the {@link SubGroup} entity.</p>
 * 
 * <p>Author: Griffin Vasalani</p>
 */
package edu.sru.cpsc.webshopping.domain.group;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import edu.sru.cpsc.webshopping.domain.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "clubs")
public class Clubs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String clubName;

    @Column(length = 200)
    private String groupDescription;


    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @ManyToMany
    @JoinTable(
        name = "car_club_members",
        joinColumns = @JoinColumn(name = "car_club_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();
   
    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "clubs", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SubGroup> SubGroups = new HashSet<>();

    // Constructors
    public Clubs() {
    }

    public Clubs(String clubName, String groupDescription, LocalDateTime creationDate, String imageUrl) {
        this.clubName = clubName;
        this.groupDescription = groupDescription;
        this.creationDate = creationDate;
        this.imageUrl = imageUrl;
    }
    
    public SubGroup createSubGroup(String name, String description, User currentUser) {

        SubGroup newSubGroup = new SubGroup(name, description, this, currentUser, LocalDateTime.now());
        this.SubGroups.add(newSubGroup);
        return newSubGroup;
    }
    
    // Getters and setters
    /**
     * Gets the unique identifier of the club.
     *
     * @return the ID of the club.
     */
    
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the club.
     *
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the club.
     *
     * @return the club's name.
     */
    public String getClubName() {
        return clubName;
    }

    /**
     * Sets the name of the club.
     *
     * @param clubName the name to set.
     */
    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    /**
     * Gets the description of the club.
     *
     * @return the club's description.
     */
    public String getGroupDescription() {
        return groupDescription;
    }

    /**
     * Sets the description of the club.
     *
     * @param groupDescription the description to set.
     */
    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    /**
     * Gets the creation date of the club.
     *
     * @return the creation date of the club.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the club.
     *
     * @param creationDate the creation date to set.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the set of members belonging to the club.
     *
     * @return the set of members.
     */
    public Set<User> getMembers() {
        return members;
    }

    /**
     * Sets the set of members belonging to the club.
     *
     * @param members the members to set.
     */
    public void setMembers(Set<User> members) {
        this.members = members;
    }

    /**
     * Gets the image URL associated with the club.
     *
     * @return the image URL.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the image URL associated with the club.
     *
     * @param imageUrl the image URL to set.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the set of subgroups within the club.
     *
     * @return the set of subgroups.
     */
    public Set<SubGroup> getSubGroups() {
        return SubGroups;
    }

    /**
     * Sets the set of subgroups within the club.
     *
     * @param SubGroups the subgroups to set.
     */
    public void setSubGroups(Set<SubGroup> SubGroups) {
        this.SubGroups = SubGroups;
    }

    // Helper methods for members
    /**
     * Adds a member to the club.
     *
     * @param user the user to add.
     */
    
    public void addMember(User user) {
        this.members.add(user);
    }

    /**
     * Removes a member from the club.
     *
     * @param user the user to remove.
     */
    public void removeMember(User user) {
        this.members.remove(user);
    }

    public boolean isMember(User user) {
        return this.members.contains(user);
    }

    // SubGroup methods
    public void addSubGroup(SubGroup SubGroup) {
        SubGroups.add(SubGroup);
        SubGroup.setClubs(this);
    }

    public void removeSubGroup(SubGroup SubGroup) {
        SubGroups.remove(SubGroup);
        SubGroup.setClubs(null);
    }

    @Override
    public String toString() {
        return "CarClub{" +
                "id=" + id +
                ", carBrand='" + clubName + '\'' +
                ", groupDescription='" + groupDescription + '\'' +
                ", creationDate=" + creationDate +
                ", imageUrl='" + imageUrl + '\'' +
                ", membersCount=" + members.size() +
                ", SubGroupsCount=" + SubGroups.size() +
                '}';
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Clubs)) return false;
        Clubs clubs = (Clubs) o;
        return getId() != null && getId().equals(clubs.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
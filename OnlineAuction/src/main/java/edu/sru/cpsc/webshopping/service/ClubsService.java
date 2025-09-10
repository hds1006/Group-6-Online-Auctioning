/**
 * The {@code ClubsService} class provides business logic for managing car clubs.
 * This service handles operations such as creating, joining, leaving, and retrieving car clubs,
 * as well as checking if a user is a member of a specific car club.
 * 
 * <p>This service interacts with the {@link ClubsRepository}, {@link UserRepository}, and {@link SubGroupRepository}
 * to perform CRUD operations on the car clubs and users. It also supports checking and modifying the membership
 * of users in car clubs.</p>
 * 
 * <p>Author: Griffin Vasalani</p>
 */

package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.group.Clubs;
import edu.sru.cpsc.webshopping.domain.group.Group;
import edu.sru.cpsc.webshopping.domain.group.SubGroup;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.group.ClubsRepository;
import edu.sru.cpsc.webshopping.repository.group.SubGroupRepository;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.sru.cpsc.webshopping.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ClubsService {

	
	@Autowired
	private UserRepository userRepository;
	
    @Autowired
    private ClubsRepository clubsRepository;
    
    @Autowired
    private SubGroupRepository subGroupRepository;

    @Autowired
    private UserService userService; // Add UserService to handle User operations
    
    /**
     * Constructs a {@code ClubsService} with the provided repositories.
     * 
     * @param clubsRepository the repository for interacting with car clubs
     * @param userRepository the repository for interacting with users
     * @param subGroupRepository the repository for interacting with subgroups
     * @param userService the service for handling user operations
     */
    public ClubsService(ClubsRepository clubsRepository, 
            UserRepository userRepository
            ) {
    		this.clubsRepository = clubsRepository;
    		this.userRepository = userRepository;

    		}
    
    /**
     * Retrieves all car clubs that the specified user is a member of.
     * 
     * @param user the user for whom to retrieve the car clubs
     * @return a list of car clubs that the user is a member of
     */
    
   public List<Clubs> getAllCarClubsForUser(User user) {
      return clubsRepository.findByMembersContaining(user);
   }

   /**
    * Retrieves a specific car club by its ID. Initializes the members of the club to avoid lazy loading issues.
    * 
    * @param id the ID of the car club to retrieve
    * @return the {@link Clubs} entity with the specified ID, or {@code null} if not found
    */
   
   @Transactional(readOnly = true)
   public Clubs getClubsById(Long id) {
       return clubsRepository.findById(id)
           .map(this::initializeMembers)
           .orElse(null);
   }
   
   /**
    * Checks if a user is a member of a specific car club.
    * 
    * @param user the user to check
    * @param clubs the car club to check against
    * @return {@code true} if the user is a member of the car club, otherwise {@code false}
    */
   
   @Transactional(readOnly = true)
   public boolean isUserMemberOfClub(User user, Clubs clubs) {
       return clubs.isMember(user);
   }

   /**
    * Adds a user as a member of a car club, if the user is not already a member.
    * 
    * @param user the user to add to the car club
    * @param clubs the car club to add the user to
    * @return {@code true} if the user was added, {@code false} if the user was already a member
    */
   @Transactional
   public boolean addMemberToClub(User user, Clubs clubs) {
       if (!clubs.isMember(user)) {
           clubs.addMember(user);
           clubsRepository.save(clubs);
           return true;
       }
       return false;
   }

   /**
    * Initializes the members of a car club to avoid lazy loading exceptions.
    * 
    * @param clubs the car club to initialize
    * @return the {@link Clubs} entity with its members initialized
    */
   
   private Clubs initializeMembers(Clubs clubs) {
       Hibernate.initialize(clubs.getMembers());
       return clubs;
   }
   
   /**
    * Saves a car club to the repository.
    * 
    * @param clubs the car club to save
    * @return the saved {@link Clubs} entity
    */
   
   public Clubs saveCarClub(Clubs clubs) {
       return clubsRepository.save(clubs);
   }
    
   /**
    * Creates a new car club with the specified name, description, and current user as the initial member.
    * 
    * @param clubName the name of the new car club
    * @param description the description of the new car club
    * @param currentUser the user who is the owner of the car club
    * @return the newly created {@link Clubs} entity
    */
   
    @Transactional
    public Clubs createCarClub(String clubName, String description, User currentUser) {
    	Clubs newCarClub = new Clubs();
    	newCarClub.setClubName(clubName);
    	newCarClub.setGroupDescription(description);
    	newCarClub.setCreationDate(LocalDateTime.now()); //Value is not allowed to be null, sets date
    	
    	
    	Set<User> members = new HashSet<>();
    	members.add(currentUser);
        newCarClub.setMembers(members);
        return clubsRepository.save(newCarClub);
    }

    /**
     * Allows a user to join an existing car club by the club's ID.
     * 
     * @param clubsId the ID of the car club to join
     * @param user the user who wants to join the car club
     */
    
@Transactional
public void joinCarClub(Long clubsId, User user) {
       Clubs clubs = clubsRepository.findById(clubsId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid car club ID: " + clubsId));
    
    if (!clubs.getMembers().contains(user)) {
        clubs.getMembers().add(user);
        clubsRepository.save(clubs);
    }
}

/**
 * Allows a user to leave a car club by the club's ID.
 * 
 * @param clubsId the ID of the car club to leave
 * @param user the user who wants to leave the car club
 */
@Transactional
public void leaveCarClub(Long clubsId, User user) {
       Clubs clubs = clubsRepository.findById(clubsId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid car club ID"));
    clubs.removeMember(user);
    clubsRepository.save(clubs);
}

/**
 * Checks if a user is a member of a car club by the club's ID.
 * 
 * @param clubsId the ID of the car club to check
 * @param user the user to check
 * @return {@code true} if the user is a member of the car club, otherwise {@code false}
 */
public boolean isUserMember(Long clubsId, User user) {
    Clubs clubs = clubsRepository.findById(clubsId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid car club ID: " + clubsId));
    
    return clubs.getMembers().contains(user);
}

}


 
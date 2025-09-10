/**
 * The {@code ClubPostsService} class provides business logic for managing posts within car clubs.
 * This service is responsible for retrieving, creating, and saving car club posts.
 * It interacts with the {@link ClubPostsRepository} to perform CRUD operations on the {@link ClubPosts} entities.
 * 
 * <p>This service class provides methods to retrieve all posts, retrieve a post by its ID, 
 * create a new post, and find posts by a specific subgroup ID.</p>
 * 
 * <p>Author: Griffin Vasalani</p>
 */

package edu.sru.cpsc.webshopping.service;  
  
import edu.sru.cpsc.webshopping.domain.group.ClubPosts;  
import edu.sru.cpsc.webshopping.repository.group.ClubPostsRepository;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Service;  
  
import java.util.List;  
  
@Service  
public class ClubPostsService {  
	
   private final ClubPostsRepository clubPostsRepository;  
  
   /**
    * Constructs a {@code ClubPostsService} with the provided {@link ClubPostsRepository}.
    * 
    * @param clubPostsRepository the repository used to interact with club posts
    */
   @Autowired  
   public ClubPostsService(ClubPostsRepository clubPostsRepository) {  
      this.clubPostsRepository = clubPostsRepository;  
   }  
  
   /**
    * Retrieves all car club posts from the repository.
    * 
    * @return a list of all {@link ClubPosts} entities
    */
   public List<ClubPosts> getAllCarClubPosts() {  
      return clubPostsRepository.findAll();  
   }  
  
   /**
    * Retrieves a specific car club post by its ID.
    * 
    * @param id the ID of the {@link ClubPosts} entity to retrieve
    * @return the {@link ClubPosts} entity with the specified ID, or {@code null} if not found
    */
   public ClubPosts getCarClubPostById(Long id) {  
      return clubPostsRepository.findById(id).orElse(null);  
   }  
  
   /**
    * Creates a new car club post.
    * 
    * @param clubPost the {@link ClubPosts} entity to create
    * @return the saved {@link ClubPosts} entity
    */
   public ClubPosts createCarClubPost(ClubPosts clubPost) {  
      return clubPostsRepository.save(clubPost);  
   }  
   
   /**
    * Finds all car club posts associated with a specific subgroup ID.
    * 
    * @param subgroupId the ID of the subgroup to filter posts by
    * @return a list of {@link ClubPosts} entities that belong to the specified subgroup
    */
   
   public List<ClubPosts> findBySubgroupId(Long subgroupId) {
       return clubPostsRepository.findBySubgroupId(subgroupId);
   }
   
}
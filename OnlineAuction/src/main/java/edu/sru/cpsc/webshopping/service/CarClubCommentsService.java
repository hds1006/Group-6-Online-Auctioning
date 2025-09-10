/**
 * The {@code CarClubCommentsService} class provides business logic related to managing comments within car clubs.
 * It is used to handle the creation and persistence of comments made by users on car club posts. 
 * This service class interacts with the {@link ClubCommentsRepository} to save comment data to the database.
 * 
 * <p>This service exposes a method to save a comment to the database. It accepts the content of the comment, 
 * the post ID to which the comment belongs, and the user making the comment.</p>
 * 
 * <p>Author: Griffin Vasalani</p>
 */

package edu.sru.cpsc.webshopping.service;  
  
import edu.sru.cpsc.webshopping.domain.group.ClubComments;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.group.ClubCommentsRepository;  
import org.springframework.stereotype.Service;  
  
import java.util.List;  
  
@Service  
public class CarClubCommentsService {  
   private final ClubCommentsRepository clubCommentsRepository;  
  
   
   /**
    * Constructs a {@code CarClubCommentsService} with the provided {@link ClubCommentsRepository}.
    * 
    * @param clubCommentsRepository the repository used to save comments
    */
   public CarClubCommentsService(ClubCommentsRepository clubCommentsRepository) {  
      this.clubCommentsRepository = clubCommentsRepository;  
   }  
  
   
   /**
    * Saves a comment made by a user to the specified car club post.
    * 
    * @param content the content of the comment
    * @param postId the ID of the post the comment is associated with
    * @param user the user who made the comment
    */
   public void saveComment(String content, Long postId, User user) {
       ClubComments comment = new ClubComments();
       comment.setContent(content);
       comment.setPostId(postId);
       comment.setUser(user); // Assuming you have the logged-in user's ID.
       clubCommentsRepository.save(comment);
   }
   
}
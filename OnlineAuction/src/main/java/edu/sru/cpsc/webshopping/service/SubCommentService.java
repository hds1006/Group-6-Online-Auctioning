/**
 * The {@code SubCommentService} class provides business logic for managing comments on SubSubGroup posts.
 * This service interacts with the {@link SubCommentRepository} to handle CRUD operations for {@link SubComment}.
 * 
 * <p>It includes methods for creating new comments and retrieving comments associated with a specific SubSubGroup post.</p>
 * 
 * <p>Author: Griffin Vasalani</p>
 */

package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.group.SubComment;
import edu.sru.cpsc.webshopping.domain.group.SubSubGroupPosts;
import edu.sru.cpsc.webshopping.repository.group.SubCommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubCommentService {

    private final SubCommentRepository subCommentRepository;

    
    /**
     * Constructs a {@code SubCommentService} with the provided {@link SubCommentRepository}.
     * 
     * @param subCommentRepository the repository for interacting with SubComments
     */
    @Autowired
    public SubCommentService(SubCommentRepository subCommentRepository) {
        this.subCommentRepository = subCommentRepository;
    }
    
    /**
     * Creates a new sub-comment and saves it to the repository.
     * 
     * @param subComment the {@link SubComment} to be created and saved
     */

    @Transactional
    public void createComment(SubComment subComment) {
        subCommentRepository.save(subComment);
    }

    /**
     * Retrieves a list of comments associated with a specific SubSubGroup post, identified by the post ID.
     * 
     * @param subSubGroupPostId the ID of the SubSubGroup post for which comments are to be retrieved
     * @return a list of {@link SubComment} objects related to the specified post
     */
    public List<SubComment> getCommentsByPostId(Long subSubGroupPostId) {
        return subCommentRepository.findBySubSubGroupPostId(subSubGroupPostId);
    }
}
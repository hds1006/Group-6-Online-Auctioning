/**
 * The {@code SubSubGroupPostsService} class provides business logic for managing posts in sub-sub-groups.
 * It interacts with {@link SubSubGroupPostsRepository} to handle operations such as creating, retrieving, 
 * and listing posts in a specific sub-sub-group.
 * 
 * <p>Author: Griffin Vasalani</p>
 */

package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.group.SubSubGroupPosts;
import edu.sru.cpsc.webshopping.repository.group.SubSubGroupPostsRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubSubGroupPostsService {

    private final SubSubGroupPostsRepository subSubGroupPostsRepository;

    /**
     * Constructs a new {@link SubSubGroupPostsService} instance with the given repository.
     *
     * @param subSubGroupPostsRepository the {@link SubSubGroupPostsRepository} used to perform CRUD operations
     */
    @Autowired
    public SubSubGroupPostsService(SubSubGroupPostsRepository subSubGroupPostsRepository) {
        this.subSubGroupPostsRepository = subSubGroupPostsRepository;
    }

    
    /**
     * Creates a new post in a sub-sub-group by saving the given {@link SubSubGroupPosts} object.
     * 
     * @param subSubGroupPost the post to be created
     */
    @Transactional
    public void createSubSubGroupPost(SubSubGroupPosts subSubGroupPost) {
        
        subSubGroupPostsRepository.save(subSubGroupPost);
    }

   
    /**
     * Retrieves a specific post by its ID.
     * 
     * @param id the ID of the post to retrieve
     * @return an {@link Optional} containing the {@link SubSubGroupPosts} if found, or {@link Optional#empty()} if not found
     */
    
    public Optional<SubSubGroupPosts> getPostById(Long id) {
        return subSubGroupPostsRepository.findById(id);
    }

    /**
     * Retrieves all posts within a specific sub-sub-group, identified by the sub-sub-group ID.
     * 
     * @param subsubgroupId the ID of the sub-sub-group for which to retrieve the posts
     * @return a list of {@link SubSubGroupPosts} for the specified sub-sub-group
     */
    @Transactional
    public List<SubSubGroupPosts> getPostsBySubsubgroupId(Long subsubgroupId) {
        return subSubGroupPostsRepository.findBySubsubgroupId(subsubgroupId);
    }

    /**
     * Retrieves all posts made by a specific user, identified by the user ID.
     * 
     * @param userId the ID of the user whose posts to retrieve
     * @return a list of {@link SubSubGroupPosts} created by the specified user
     */
    
    public List<SubSubGroupPosts> getPostsByUserId(Long userId) {
        return subSubGroupPostsRepository.findByUserId(userId);
    }
}
//this class has the method for archiving old post after a certain time
package edu.sru.cpsc.webshopping.service;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.sru.cpsc.webshopping.domain.Post;
import edu.sru.cpsc.webshopping.repository.PostRepository;
	@Service
	public class PostArchiverService {

	    @Autowired
	    private PostRepository postRepository;

	    // Runs every day 
	    @Scheduled(fixedRate = 24L * 60 * 60 * 1000)  // addes up to 1 day but in milliseconds
	    public void archiveOldPosts() {
	        //cuttoff is  set to 30 days
	        Date cutoff = new Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
	        // finds post that have not been commented on since cutoff (30 days) from the postRepository and puts them in a list called oldPpost
	        List<Post> oldPosts = postRepository.findPostsNotCommentedSince(cutoff);
	        //sets all post in oldPost to be archived and saves
	        for (Post post : oldPosts) {
	            post.setArchived(true);
	            postRepository.save(post);
	            /*this was used to see in the console that it was archiving the posts
	             * System.out.println("Archived post ID " + post.getId());*/
	             
	        }
	    }
	}


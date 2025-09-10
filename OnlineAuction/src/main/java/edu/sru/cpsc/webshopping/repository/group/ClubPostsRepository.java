package edu.sru.cpsc.webshopping.repository.group;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.sru.cpsc.webshopping.domain.group.ClubPosts; 

public interface ClubPostsRepository extends JpaRepository<ClubPosts, Long> {  
	List<ClubPosts> findBySubgroupId(Long subgroupId);
}
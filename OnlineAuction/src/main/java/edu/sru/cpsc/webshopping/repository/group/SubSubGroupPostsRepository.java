package edu.sru.cpsc.webshopping.repository.group;

import edu.sru.cpsc.webshopping.domain.group.SubSubGroup;
import edu.sru.cpsc.webshopping.domain.group.SubSubGroupPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubSubGroupPostsRepository extends JpaRepository<SubSubGroupPosts, Long> {

    List<SubSubGroupPosts> findBySubsubgroupId(Long subsubgroupId);

    List<SubSubGroupPosts> findByUserId(Long userId);
    
    @Query("SELECT p FROM SubSubGroupPosts p LEFT JOIN FETCH p.comments WHERE p.subsubgroupId = :subsubgroupId")
    List<SubSubGroupPosts> findPostsBySubsubgroupId(@Param("subsubgroupId") Long subsubgroupId);
    
}
package edu.sru.cpsc.webshopping.repository.group;

import edu.sru.cpsc.webshopping.domain.group.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
    List<SubComment> findBySubSubGroupPostId(Long subSubGroupPostId);
}
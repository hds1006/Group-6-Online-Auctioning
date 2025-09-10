package edu.sru.cpsc.webshopping.repository.group;  
  
import edu.sru.cpsc.webshopping.domain.group.ClubComments;  
import org.springframework.data.jpa.repository.JpaRepository;  
import org.springframework.stereotype.Repository;  
  
@Repository  
public interface ClubCommentsRepository extends JpaRepository<ClubComments, Long> {  
}
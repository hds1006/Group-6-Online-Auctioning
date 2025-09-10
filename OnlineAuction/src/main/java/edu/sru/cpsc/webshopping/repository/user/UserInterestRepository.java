package edu.sru.cpsc.webshopping.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sru.cpsc.webshopping.domain.user.UserInterests;
@Repository  
public interface UserInterestRepository extends CrudRepository<UserInterests, Long> {  
    @Query("SELECT ui FROM UserInterests ui WHERE ui.user.id = ?1")
    UserInterests findByUserId(Long userId);
}
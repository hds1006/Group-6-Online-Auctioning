package edu.sru.cpsc.webshopping.repository.group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.sru.cpsc.webshopping.domain.group.Clubs;
import edu.sru.cpsc.webshopping.domain.user.User;

public interface ClubsRepository extends JpaRepository<Clubs, Long> {
	List<Clubs> findByMembersContaining(User user);
}
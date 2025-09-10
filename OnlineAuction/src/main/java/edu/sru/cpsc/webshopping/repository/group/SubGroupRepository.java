package edu.sru.cpsc.webshopping.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.sru.cpsc.webshopping.domain.group.SubGroup;
import java.util.List;

public interface SubGroupRepository extends JpaRepository<SubGroup, Long> {
    List<SubGroup> findByClubsId(Long clubsId);
    List<SubGroup> findByOwnerId(Long ownerId);
    List<SubGroup> findByNameContainingIgnoreCase(String name);
}
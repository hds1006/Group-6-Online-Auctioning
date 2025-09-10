package edu.sru.cpsc.webshopping.repository.group;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.sru.cpsc.webshopping.domain.group.SubSubGroup;
import java.util.List;

public interface SubSubGroupRepository extends JpaRepository<SubSubGroup, Long> {
    List<SubSubGroup> findBySubGroupId(Long subGroupId);
    List<SubSubGroup> findByNameContainingIgnoreCase(String name);
}
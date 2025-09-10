/**
 * The {@code SubSubGroupService} class provides business logic for managing sub-sub-groups within a larger group structure.
 * It interacts with {@link SubSubGroupRepository} to handle operations such as retrieving, creating, saving, deleting, 
 * and searching sub-sub-groups.
 *
 * <p>Author: Griffin Vasalani</p>
 */

package edu.sru.cpsc.webshopping.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.sru.cpsc.webshopping.domain.group.SubSubGroup;
import edu.sru.cpsc.webshopping.domain.group.SubGroup;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.group.SubSubGroupRepository;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubSubGroupService {
    
    @Autowired
    private SubSubGroupRepository subSubGroupRepository;

    
    /*public SubSubGroup createSubSubGroup(SubGroup subGroup, String name, String description, User owner) {
        SubSubGroup subSubGroup = new SubSubGroup(
            name, 
            description, 
            subGroup, 
            subGroup.getClubs(), 
            owner, 
            LocalDateTime.now()
        );
        return subSubGroupRepository.save(subSubGroup);
    }
 	*/
    
    /**
     * Retrieves a sub-sub-group by its unique ID.
     * 
     * @param id the ID of the sub-sub-group to retrieve
     * @return the {@link SubSubGroup} associated with the given ID
     * @throws IllegalArgumentException if the sub-sub-group is not found
     */
    
    @Transactional
    public SubSubGroup getSubSubGroupById(Long id) {
        return subSubGroupRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Sub-sub group not found with id: " + id));
    }

    /**
     * Retrieves all sub-sub-groups that belong to a specific sub-group, identified by the sub-group ID.
     * 
     * @param subGroupId the ID of the sub-group for which to retrieve sub-sub-groups
     * @return a list of {@link SubSubGroup} associated with the given sub-group ID
     */
    
    public List<SubSubGroup> getSubSubGroupsBySubGroup(Long subGroupId) {
        return subSubGroupRepository.findBySubGroupId(subGroupId);
    }

    /**
     * Saves the given {@link SubSubGroup} object to the repository.
     * 
     * @param subSubGroup the sub-sub-group to save
     */
    
    public void saveSubSubGroup(SubSubGroup subSubGroup) {
        subSubGroupRepository.save(subSubGroup);
    }
    
    /**
     * Retrieves all sub-sub-groups.
     * 
     * @return a list of all {@link SubSubGroup}
     */
    
    public List<SubSubGroup> findAll() {  
        return subSubGroupRepository.findAll();  
     }  
    
    /**
     * Deletes a specific sub-sub-group.
     * 
     * @param subSubGroup the sub-sub-group to delete
     */
    
    public void deleteSubSubGroup(SubSubGroup subSubgroup) {
        subSubGroupRepository.delete(subSubgroup);
    }
    
    /**
     * Searches for sub-sub-groups by a given query string. The search is case-insensitive.
     * 
     * @param searchQuery the query string to search for in the name of the sub-sub-group
     * @return a list of {@link SubSubGroup} that match the search query
     */
    
    public List<SubSubGroup> searchSubSubGroups(String searchQuery) {
        return subSubGroupRepository.findByNameContainingIgnoreCase(searchQuery);
    }
    
    /**
     * Retrieves all sub-sub-groups in the system.
     * 
     * @return a list of all {@link SubSubGroup}
     */
    
    public List<SubSubGroup> getAllSubSubGroups() {
        return subSubGroupRepository.findAll();
    }
    
}
/**
 * The {@code SubGroupService} class provides business logic for managing sub-groups within a car club.
 * This service interacts with {@link SubGroupRepository} and {@link ClubsRepository} to handle operations related to 
 * sub-groups, such as retrieving, saving, searching, deleting, and managing images associated with sub-groups.
 * 
 * <p>Author: Griffin Vasalani</p>
 */

package edu.sru.cpsc.webshopping.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import edu.sru.cpsc.webshopping.domain.group.Clubs;
import edu.sru.cpsc.webshopping.domain.group.SubGroup;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.group.ClubsRepository;
import edu.sru.cpsc.webshopping.repository.group.SubGroupRepository;

@Service  
public class SubGroupService {  
  
private static final Logger log = LoggerFactory.getLogger(SubGroupService.class);
	
   @Autowired  
   private SubGroupRepository subgroupRepository;  
  
   @Autowired  
   private ClubsRepository clubsRepository;  
  
   
   /**
    * Retrieves all sub-groups associated with a specific car club, identified by the club ID.
    * 
    * @param clubsId the ID of the car club for which to retrieve the sub-groups
    * @return a list of {@link SubGroup} objects associated with the specified car club
    */
   
   public List<SubGroup> getSubgroupsByCarClub(Long clubsId) {  
      return subgroupRepository.findByClubsId(clubsId);  
   }  
  
   /**
    * Retrieves a sub-group by its ID.
    * 
    * @param id the ID of the sub-group to retrieve
    * @return the {@link SubGroup} with the specified ID, or {@code null} if no such sub-group exists
    */
   
   public SubGroup getSubGroupById(Long id) {
       Optional<SubGroup> optionalSubGroup = subgroupRepository.findById(id);
       return optionalSubGroup.orElse(null); 
   }
   
   /**
    * Saves a given sub-group to the repository.
    * 
    * @param subgroup the {@link SubGroup} to save
    * @return the saved {@link SubGroup} object
    */
   
   public SubGroup saveSubGroup(SubGroup subgroup) {
       return subgroupRepository.save(subgroup);
   }
   
   /**
    * Searches for sub-groups based on a search query. If the query is empty or {@code null}, all sub-groups are returned.
    * 
    * @param query the search query to filter sub-groups by name
    * @return a list of {@link SubGroup} objects matching the search query
    */
   public List<SubGroup> searchSubGroups(String query) {
       if (query == null || query.trim().isEmpty()) {
           return getAllSubGroups();
       }
       return subgroupRepository.findByNameContainingIgnoreCase(query.trim());
   }
   
   /**
    * Retrieves all sub-groups in the repository.
    * 
    * @return a list of all {@link SubGroup} objects
    */
   
   public List<SubGroup> getAllSubGroups() {
       return subgroupRepository.findAll();
   }
   
   /**
    * Deletes a given sub-group from the repository.
    * 
    * @param subgroup the {@link SubGroup} to delete
    */
   public void deleteSubGroup(SubGroup subgroup) {
       subgroupRepository.delete(subgroup);
   }
   
   /**
    * Saves an image associated with a sub-group. The image is saved to a directory specific to the sub-group ID.
    * 
    * @param image the image file to upload
    * @param subgroup the {@link SubGroup} to associate with the image
    * @return the path to the saved image file, or {@code null} if the upload fails
    */
   public String saveSubGroupImage(MultipartFile image, SubGroup subgroup) {
	    try {
	        String baseUploadDir = System.getProperty("user.dir") + "/uploads/subgroups/";
	        File directory = new File(baseUploadDir + subgroup.getId());
	        if (!directory.exists()) {
	            directory.mkdirs();
	        }

	        String filename = StringUtils.cleanPath(image.getOriginalFilename());
	        Path filePath = Paths.get(directory.getAbsolutePath(), filename);

	        Files.createDirectories(filePath.getParent());
	        
	        image.transferTo(filePath.toFile());
	        return filePath.toString();
	    } catch (IOException e) {
	        log.error("Image upload failed", e);
	        return null;
	    }
	}
}

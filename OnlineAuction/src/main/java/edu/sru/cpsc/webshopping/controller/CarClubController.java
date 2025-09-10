/**
 * Controller for managing car club-related functionalities in the web shopping application.
 * 
 * This controller handles operations related to car clubs, subgroups, sub-subgroups, 
 * posts, and comments within the social networking component of the application.
 * 
 * @author Griffin Vasalani
 * @version 1.0
 * @since 2024
 */

package edu.sru.cpsc.webshopping.controller;

import edu.sru.cpsc.webshopping.domain.group.Clubs;
import edu.sru.cpsc.webshopping.domain.group.SubComment;
import edu.sru.cpsc.webshopping.domain.group.ClubComments;  
import edu.sru.cpsc.webshopping.domain.group.ClubPosts;  
import edu.sru.cpsc.webshopping.domain.group.SubGroup;
import edu.sru.cpsc.webshopping.domain.group.SubSubGroup;
import edu.sru.cpsc.webshopping.domain.group.SubSubGroupPosts;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.group.ClubCommentsRepository;
import edu.sru.cpsc.webshopping.repository.group.ClubsRepository;  
import edu.sru.cpsc.webshopping.repository.group.SubGroupRepository;  
import edu.sru.cpsc.webshopping.repository.products.ProductsRepository;  
import edu.sru.cpsc.webshopping.repository.user.UserRepository;  
import edu.sru.cpsc.webshopping.service.CarClubCommentsService;  
import edu.sru.cpsc.webshopping.service.ClubPostsService;  
import edu.sru.cpsc.webshopping.service.ClubsService;
import edu.sru.cpsc.webshopping.service.SubCommentService;
import edu.sru.cpsc.webshopping.service.SubGroupService;
import edu.sru.cpsc.webshopping.service.SubSubGroupPostsService;
import edu.sru.cpsc.webshopping.service.SubSubGroupService;
import edu.sru.cpsc.webshopping.service.UserService; // Import the user service  
import jakarta.transaction.Transactional;  
  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;  
import org.springframework.ui.Model;  
import org.springframework.web.bind.annotation.DeleteMapping;  
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.ModelAttribute;  
import org.springframework.web.bind.annotation.PathVariable;  
import org.springframework.web.bind.annotation.PostMapping;  
import org.springframework.web.bind.annotation.PutMapping;  
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;  
  
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
  
@Controller  
// @SessionAttributes("carClubs")  
public class CarClubController {  
  
   Logger log = LoggerFactory.getLogger(CarClubController.class);  
  
   @Autowired  
   private ProductsRepository productsRepository;  
  
   @Autowired  
   private UserRepository userRepository;  
  
   @Autowired  
   private ClubsService carClubService;  
  
   @Autowired  
   private SubGroupService subgroupService;  
  
   @Autowired  
   private ClubsRepository clubsRepository;  
  
   @Autowired  
   private SubGroupRepository subgroupRepository;  
  
   @Autowired  
   private UserService userService;  
  
   @Autowired  
   private ClubPostsService carClubPostsService;  
  
   @Autowired  
   private CarClubCommentsService carClubCommentsService;  
   
   @Autowired
   private ClubCommentsRepository clubCommentsRepository;
  
   @Autowired
   private SubSubGroupService subSubGroupService;
   
   @Autowired
   private SubSubGroupPostsService subSubGroupPostsService;
   
   @Autowired
   private SubCommentService subCommentService;
   
   @Autowired
   private SimpMessagingTemplate messagingTemplate;
   
   
   @PostMapping("/createCarClub")  
   public String createCarClub(@RequestParam String carBrand, @RequestParam String groupDescription, Principal principal,  RedirectAttributes redirectAttributes) {  
      // Get current user  
      User owner = userService.getUserByUsername(principal.getName()); // Fetch user by username  
      User currentUser = userService.getUserByUsername(principal.getName());  
      if (owner == null) {  
          
        redirectAttributes.addFlashAttribute("errorMessage", "Current user not found.");  
        return "redirect:/errorPage";  
      }  
       
      carClubService.createCarClub(carBrand, groupDescription, currentUser);  
      System.out.println("Car Club Created Succesfully");  
      return "redirect:/Social";  
   }  
   
   
   
  /*
   @PostMapping("/joinCarClub")
   @Transactional
   public String joinCarClub(@RequestParam Long clubsId, Principal principal, RedirectAttributes redirectAttributes) {
       CarClub carClub = carClubService.getClubsById(clubsId);
       User currentUser = userService.getUserByUsername(principal.getName());

       if (carClub.getMembers().contains(currentUser)) {
    	    redirectAttributes.addFlashAttribute("errorMessage", "You are already a member of this club.");
    	    return "redirect:/carClub?clubsId=" + clubsId;
    	}
       
       if (carClub == null || currentUser == null) {
           redirectAttributes.addFlashAttribute("errorMessage", "Car club or user not found.");
           return "redirect:/Social";
       }

       // Check if user is already a member
       if (carClub.getMembers().contains(currentUser)) {
           redirectAttributes.addFlashAttribute("errorMessage", "You are already a member of this club.");
       } else {
           carClub.addMember(currentUser);
           carClubService.saveCarClub(carClub);
           redirectAttributes.addFlashAttribute("successMessage", "Successfully joined the club!");
       }

       return "redirect:/carClub?clubsId=" + clubsId;
   }

   @PostMapping("/leaveCarClub")
   @Transactional
   public String leaveCarClub(@RequestParam Long clubsId, Principal principal, RedirectAttributes redirectAttributes) {
       CarClub carClub = carClubService.getClubsById(clubsId);
       User currentUser = userService.getUserByUsername(principal.getName());

       if (carClub == null || currentUser == null) {
           redirectAttributes.addFlashAttribute("errorMessage", "Car club or user not found.");
           return "redirect:/Social";
       }

       // Check if user is the owner
       if (carClub.getOwner().equals(currentUser)) {
           Set<User> members = carClub.getMembers();
           if (members.size() <= 1) {
               redirectAttributes.addFlashAttribute("errorMessage", "As the owner, you cannot leave the club while being the only member.");
               return "redirect:/carClub?clubsId=" + clubsId;
           }
           // Transfer ownership
           User newOwner = members.stream()
               .filter(member -> !member.equals(currentUser))
               .findFirst()
               .orElse(null);
           carClub.setOwner(newOwner);
       }

       // Check if user is a member before removing
       if (!carClub.getMembers().contains(currentUser)) {
           redirectAttributes.addFlashAttribute("errorMessage", "You are not a member of this club.");
       } else {
           carClub.removeMember(currentUser);
           carClubService.saveCarClub(carClub);
           redirectAttributes.addFlashAttribute("successMessage", "Successfully left the club!");
       }

       return "redirect:/carClub?clubsId=" + clubsId;
   }
  */
   
   /**
    * Displays details of a specific car club.
    * 
    * This method retrieves and displays information about a car club, including 
    * its members, subgroups, and current user's membership status.
    * 
    * @param clubsId The unique identifier of the car club to be displayed
    * @param model The Spring MVC model for passing data to the view
    * @param principal The currently authenticated user viewing the club
    * @return The view name for the car club page, or a redirect to the Social page if club is not found
    */
   
   @GetMapping("/carClub")
   public String showCarClub(@RequestParam Long clubsId, Model model, Principal principal) {
       Clubs clubs = carClubService.getClubsById(clubsId);
       User currentUser = userService.getUserByUsername(principal.getName());
       
       if (clubs == null || currentUser == null) {
           return "redirect:/Social";
       }
       
       List<SubGroup> subgroups = subgroupService.getSubgroupsByCarClub(clubsId);
       
       model.addAttribute("currentUser", currentUser);
       model.addAttribute("user", currentUser);
       model.addAttribute("clubs", clubs);
       model.addAttribute("members", clubs.getMembers());
       model.addAttribute("subgroups", subgroups);
       model.addAttribute("isCurrentUserMember", clubs.isMember(currentUser));
       model.addAttribute("clubsId", clubsId);
       
       return "Clubs";
   }
   
   /**
    * Displays a car club by its specific ID.
    * 
    * An alternative method to retrieve and display a car club's details, 
    * similar to {@link #showCarClub(Long, Model, Principal)}.
    * 
    * @param id The unique identifier of the car club to be displayed
    * @param model The Spring MVC model for passing data to the view
    * @param principal The currently authenticated user viewing the club
    * @return The view name for the car club page, or a redirect to the Social page if club is not found
    */
   
   @GetMapping("/carClub/{id}")
   public String showCarClubById(@PathVariable Long id, Model model, Principal principal) {
       Clubs clubs = carClubService.getClubsById(id);
       User currentUser = userService.getUserByUsername(principal.getName());

       if (clubs == null || currentUser == null) {
           return "redirect:/Social"; // Redirect if club or user is not found
       }

       List<SubGroup> subgroups = subgroupService.getSubgroupsByCarClub(id); // Fetch subgroups for the car club

       model.addAttribute("currentUser", currentUser);
       model.addAttribute("carClub", clubs);
       model.addAttribute("members", clubs.getMembers());
       model.addAttribute("subgroups", subgroups);
       model.addAttribute("isCurrentUserMember", clubs.isMember(currentUser));

       return "Clubs"; // Return the view name for the car club page
   }
  
   /**
    * Creates a new subgroup within a car club.
    * 
    * Allows a user to create a subgroup in an existing car club, with an optional 
    * image upload. The current user becomes the owner of the newly created subgroup.
    * 
    * @param clubsId The ID of the parent car club where the subgroup will be created
    * @param subGroupName Name of the new subgroup
    * @param subGroupDescription Description of the new subgroup
    * @param subGroupImage Optional image file for the subgroup
    * @param model The Spring MVC model for passing data to the view
    * @param principal The currently authenticated user creating the subgroup
    * @return Redirects to the car club page after subgroup creation
    */
   
   @PostMapping("/createSubGroup")   
   @Transactional   
   public String createSubGroup(@RequestParam Long clubsId,   
           @RequestParam String subGroupName,   
           @RequestParam String subGroupDescription,   
           @RequestParam(required = false) MultipartFile subGroupImage, // New image parameter
           Model model,   
           Principal principal) {  
      Clubs clubs = clubsRepository.findById(clubsId)   
        .orElseThrow(() -> new IllegalArgumentException("Invalid car club Id:" + clubsId));   
       
      User currentUser = userService.getUserByUsername(principal.getName());   
       
      SubGroup newSubGroup = clubs.createSubGroup(subGroupName, subGroupDescription, currentUser);   
      newSubGroup.addMember(currentUser); 
      
      
      subgroupRepository.save(newSubGroup);   
       
      model.addAttribute("successMessage", "Subgroup created successfully!");   
      model.addAttribute("currentUser", currentUser); 
      model.addAttribute("user", currentUser);
      
      return "redirect:/carClub?clubsId=" + clubsId;   
   }  
  
  
   @GetMapping("/carClubSubGroups/{subgroupId}")  
   public String showCarClubSubGroups(@PathVariable Long subgroupId, Model model, Principal principal) {   
          
      if (subgroupId == null) {   
         return "redirect:/errorPage";  
      }   
          
      SubGroup subgroup = subgroupService.getSubGroupById(subgroupId);  
      Hibernate.initialize(subgroup.getMembers());  
          
      List<ClubPosts> posts = carClubPostsService.findBySubgroupId(subgroupId);
      User currentUser = userService.getUserByUsername(principal.getName());
      boolean isMember = subgroup.isMember(currentUser);
      model.addAttribute("currentUser", currentUser); 
      model.addAttribute("user", currentUser);
      
      model.addAttribute("carClub", subgroup.getClubs());  
      model.addAttribute("subgroup", subgroup); 
      model.addAttribute("isMember", isMember);
      model.addAttribute("members", subgroup.getMembers());
      model.addAttribute("posts", posts);
      
          
      return "redirect:/subgroup?subgroupId=" + subgroupId;   
   }
  
   /**
    * Displays a specific car club subgroup.
    * 
    * Retrieves and displays details of a subgroup, including its posts, 
    * members, and search functionality.
    * 
    * @param subgroupId The ID of the subgroup to be displayed
    * @param searchQuery Optional search query to filter subgroups
    * @param model The Spring MVC model for passing data to the view
    * @param principal The currently authenticated user viewing the subgroup
    * @return The view for club subgroups or a redirect to an error page
    */
   
   @GetMapping("/subgroup")  
   @Transactional  
   public String showSubGroup(@RequestParam(required = false) Long subgroupId, 
           @RequestParam(required = false) String searchQuery, 
           Model model, 
           Principal principal) {   
       
	   List<SubSubGroup> subsubgroups = subSubGroupService.getSubSubGroupsBySubGroup(subgroupId); 
	   List<SubGroup> subgroups = subgroupService.getSubgroupsByCarClub(subgroupId);	
	   
	   
	   
	   
       if (searchQuery != null && !searchQuery.isEmpty() && subgroupId == null) {
           List<SubGroup> allGroups = subgroupService.getAllSubGroups();
           List<SubGroup> filteredGroups = subgroupService.searchSubGroups(searchQuery);
           
           model.addAttribute("subgroups", allGroups);
           model.addAttribute("filteredGroups", filteredGroups);
           model.addAttribute("searchQuery", searchQuery);
           
           model.addAttribute("posts", new ArrayList<>());
           model.addAttribute("members", new ArrayList<>());
           model.addAttribute("isMember", false);
           
           return "carClubSubGroups";
       }
      
       if (subgroupId == null) {   
           return "redirect:/errorPage";  
       }   
        
       User currentUser = userService.getUserByUsername(principal.getName());
       List<ClubPosts> posts = carClubPostsService.findBySubgroupId(subgroupId);
       posts.forEach(post -> Hibernate.initialize(post.getComments()));
       SubGroup subgroup = subgroupService.getSubGroupById(subgroupId);  
       
      
       Hibernate.initialize(subgroup.getMembers());  
        
       boolean isMember = subgroup.getMembers().stream()
    		    .anyMatch(member -> member.getId() == currentUser.getId());
       
       model.addAttribute("subgroups", subgroups);
       model.addAttribute("currentUser", currentUser); 
       model.addAttribute("user", currentUser);
       model.addAttribute("carClub", subgroup.getClubs());  
       model.addAttribute("subgroup", subgroup);   
       model.addAttribute("posts", posts);
       model.addAttribute("members", subgroup.getMembers());
       model.addAttribute("isMember", isMember);
       model.addAttribute("subsubgroups", subsubgroups);
       model.addAttribute("parentSubgroupId", subgroupId);
       
       return "ClubsSubGroups";   
   }

   /**
    * Allows a user to join a specific subgroup.
    * 
    * Processes a request for a user to become a member of a subgroup, 
    * with checks to prevent duplicate memberships.
    * 
    * @param subgroupId The ID of the subgroup to join
    * @param principal The currently authenticated user attempting to join
    * @param redirectAttributes Attributes for passing success or error messages
    * @return Redirects to the subgroup page with updated membership status
    */
   
   @PostMapping("/joinSubGroup")
   @Transactional
   public String joinSubGroup(@RequestParam Long subgroupId, Principal principal, RedirectAttributes redirectAttributes) {
       SubGroup subgroup = subgroupService.getSubGroupById(subgroupId);
       User currentUser = userService.getUserByUsername(principal.getName());

       if (subgroup == null || currentUser == null) {
           redirectAttributes.addFlashAttribute("errorMessage", "Group or user not found.");
           return "redirect:/carClub?clubsId=" + subgroup.getClubs().getId();
       }

       boolean isMember = subgroup.getMembers().stream()
    		    .anyMatch(member -> member.getId() == currentUser.getId());

       if (isMember) {
           redirectAttributes.addFlashAttribute("errorMessage", "You are already a member of this Group.");
       } else {
           subgroup.addMember(currentUser);
           subgroupService.saveSubGroup(subgroup);
           redirectAttributes.addFlashAttribute("successMessage", "You have successfully joined the Group!");
       }

       redirectAttributes.addFlashAttribute("isMember", true);
       return "redirect:/subgroup?subgroupId=" + subgroupId;
   }

   
   /**
    * Allows a user to leave a specific subgroup.
    * 
    * Handles the process of a user leaving a subgroup, including special 
    * handling for the group owner to transfer ownership.
    * 
    * @param subgroupId The ID of the subgroup to leave
    * @param principal The currently authenticated user attempting to leave
    * @param redirectAttributes Attributes for passing success or error messages
    * @return Redirects to the car club page with updated membership status
    */
   
   @PostMapping("/leaveSubGroup")
   @Transactional
   public String leaveSubGroup(@RequestParam Long subgroupId, Principal principal, RedirectAttributes redirectAttributes) {
       SubGroup subgroup = subgroupService.getSubGroupById(subgroupId);
       User currentUser = userService.getUserByUsername(principal.getName());

       if (subgroup == null || currentUser == null) {
           redirectAttributes.addFlashAttribute("errorMessage", "Group or user not found.");
           return "redirect:/carClub?clubsId=" + (subgroup != null ? subgroup.getClubs().getId() : null);
       }

       boolean isMember = subgroup.getMembers().stream()
               .anyMatch(member -> member.getId() == currentUser.getId());

       if (!isMember) {
           redirectAttributes.addFlashAttribute("errorMessage", "You are not a member of this Group.");
       } else {
           if (subgroup.getOwner().equals(currentUser)) {
               // Transfer ownership to another member
               if (!subgroup.getMembers().isEmpty()) {
                   User newOwner = subgroup.getMembers().iterator().next();
                   subgroup.setOwner(newOwner);
                   redirectAttributes.addFlashAttribute("successMessage", "Ownership transferred to " + newOwner.getUsername() + ".");
               } else {
                   redirectAttributes.addFlashAttribute("errorMessage", "You are the last member. Cannot transfer ownership.");
               }
           }

           subgroup.removeMember(currentUser);

           // Check if the subgroup has no members left
           if (subgroup.getMembers().isEmpty()) {
               // If the current user is the last member, delete the subgroup
               redirectAttributes.addFlashAttribute("successMessage", "You have successfully left the Group. The Group has been deleted.");
               subgroupService.deleteSubGroup(subgroup);  
           } else {
               subgroupService.saveSubGroup(subgroup);
               redirectAttributes.addFlashAttribute("successMessage", "You have successfully left the Group.");
           }
       }

       redirectAttributes.addFlashAttribute("isMember", false);
       return "redirect:/carClub?clubsId=" + (subgroup.getClubs() != null ? subgroup.getClubs().getId() : null);
   }
   
   /**
    * Creates a new post in a car club subgroup.
    * 
    * Allows a user to create and submit a post in a specific subgroup.
    * 
    * @param carClubPost The post to be created
    * @param principal The currently authenticated user creating the post
    * @param model The Spring MVC model for passing data to the view
    * @return Redirects to the subgroup page
    */
   
   @PostMapping("/carClubPosts")
   public String getAllCarClubPosts(@ModelAttribute ClubPosts carClubPost, Principal principal, Model model) {
       
       User currentUser = userService.getUserByUsername(principal.getName());
       carClubPost.setUser(currentUser);
       carClubPostsService.createCarClubPost(carClubPost);
       
       model.addAttribute("currentUser", currentUser); 
       model.addAttribute("user", currentUser);
       
       
       return "redirect:/subgroup?subgroupId=" + carClubPost.getSubgroupId();
   }
    
   /**
    * Retrieves a specific car club post by its ID.
    * 
    * @param id The unique identifier of the post
    * @param model The Spring MVC model for passing data to the view
    * @return Redirects to the subgroup page containing the post
    */
   
   @GetMapping("/carClubPosts/{id}")  
   public String getCarClubPostById(@PathVariable Long id, Model model) {  
      ClubPosts carClubPost = carClubPostsService.getCarClubPostById(id);  
      model.addAttribute("carClubPost", carClubPost);  
      return "redirect:/subgroup?subgroupId=" + carClubPost.getSubgroupId();  
   }  
     
   /**
    * Creates a new sub-subgroup within a subgroup.
    * 
    * Allows a user to create a new group nested within an existing subgroup. 
    * The current user becomes the owner of the new sub-subgroup.
    * 
    * @param subgroupId The ID of the parent subgroup
    * @param subSubGroupName Name of the new sub-subgroup
    * @param subSubGroupDescription Description of the new sub-subgroup
    * @param model The Spring MVC model for passing data to the view
    * @param principal The currently authenticated user creating the sub-subgroup
    * @return Redirects to the parent subgroup page
    */
   
   @PostMapping("/createSubSubGroup")   
   @Transactional   
   public String createSubSubGroup(@RequestParam Long subgroupId,   
                         @RequestParam String subSubGroupName,   
                         @RequestParam String subSubGroupDescription,   
                         Model model,   
                         Principal principal) {   
       SubGroup subGroup = subgroupService.getSubGroupById(subgroupId);   
       User currentUser = userService.getUserByUsername(principal.getName());   
        
       SubSubGroup newSubSubGroup = subGroup.createSubSubGroup(
           subSubGroupName, 
           subSubGroupDescription, 
           currentUser
       );
       newSubSubGroup.addMember(currentUser);   
       subSubGroupService.saveSubSubGroup(newSubSubGroup);   
        
       model.addAttribute("successMessage", "Group created successfully!");   
       model.addAttribute("currentUser", currentUser); 
       
       return "redirect:/subgroup?subgroupId=" + subgroupId;   
   }

   /**
    * Displays a specific sub-subgroup.
    * 
    * Retrieves and displays details of a sub-subgroup, with optional search functionality.
    * 
    * @param subsubgroupId The ID of the sub-subgroup to be displayed
    * @param searchQuery Optional search query to filter sub-subgroups
    * @param model The Spring MVC model for passing data to the view
    * @param principal The currently authenticated user viewing the sub-subgroup
    * @return The view for club sub-subgroups
    */
   
   @GetMapping("/subsubgroup")
   public String showSubSubGroup(@RequestParam(required = false) Long subsubgroupId,
                                 @RequestParam(required = false) String searchQuery,
                                 Model model, Principal principal) {

       List<SubSubGroup> subsubgroups;

       if (searchQuery != null && !searchQuery.isEmpty()) {
           subsubgroups = subSubGroupService.searchSubSubGroups(searchQuery);
       } else {
           subsubgroups = subSubGroupService.getAllSubSubGroups();
       }

       if (subsubgroupId != null) {
           SubSubGroup subSubGroup = subSubGroupService.getSubSubGroupById(subsubgroupId);
           if (subSubGroup == null) {
               return "redirect:/errorPage";
           }

           User currentUser = userService.getUserByUsername(principal.getName());
           boolean isMember = subSubGroup.getMembers().stream()
                   .anyMatch(member -> member.getId() == currentUser.getId());

           List<SubSubGroupPosts> posts = subSubGroupPostsService.getPostsBySubsubgroupId(subsubgroupId);

           Long parentSubgroupId = subSubGroup.getSubGroup().getId();
           
           model.addAttribute("parentSubgroupId", parentSubgroupId);
           model.addAttribute("currentUser", currentUser);
           model.addAttribute("user", currentUser);
           model.addAttribute("subSubGroup", subSubGroup);
           model.addAttribute("isMember", isMember);
           model.addAttribute("members", subSubGroup.getMembers());
           model.addAttribute("posts", posts);
       }

       model.addAttribute("subsubgroups", subsubgroups);
       model.addAttribute("searchQuery", searchQuery);

       return "ClubsSubSubGroups";
   }
   
   @GetMapping("/ClubsSubSubGroups/{subSubGroupId}")  
   public String showCarClubSubSubGroups(@PathVariable Long subSubGroupId, Model model, Principal principal) {   
       SubSubGroup subSubGroup = subSubGroupService.getSubSubGroupById(subSubGroupId);
       User currentUser = userService.getUserByUsername(principal.getName());
       
       boolean isMember = subSubGroup.getMembers().stream()
               .anyMatch(member -> member.getId() == currentUser.getId());
       
       model.addAttribute("currentUser", currentUser); 
       model.addAttribute("user", currentUser);
       model.addAttribute("carClub", subSubGroup.getClubs());  
       model.addAttribute("subgroup", subSubGroup.getSubGroup());
       model.addAttribute("subSubGroup", subSubGroup); 
       model.addAttribute("isMember", isMember);
       model.addAttribute("members", subSubGroup.getMembers());
       
       return "redirect:/subsubgroup?subsubgroupId=" + subSubGroupId;   
   }
   
   /**
    * Allows a user to join a specific sub-subgroup.
    * 
    * Processes a request for a user to become a member of a sub-subgroup, 
    * with checks to prevent duplicate memberships.
    * 
    * @param subSubGroupId The ID of the sub-subgroup to join
    * @param principal The currently authenticated user attempting to join
    * @param redirectAttributes Attributes for passing success or error messages
    * @return Redirects to the sub-subgroup page with updated membership status
    */

   @PostMapping("/joinSubSubGroup")
   @Transactional
   public String joinSubSubGroup(@RequestParam Long subSubGroupId, Principal principal, RedirectAttributes redirectAttributes) {
       SubSubGroup subSubGroup = subSubGroupService.getSubSubGroupById(subSubGroupId);
       User currentUser = userService.getUserByUsername(principal.getName());

       if (subSubGroup == null || currentUser == null) {
           redirectAttributes.addFlashAttribute("errorMessage", "Group or user not found.");
           return "redirect:/subgroup?subgroupId=" + subSubGroup.getSubGroup().getId();
       }

       boolean isMember = subSubGroup.getMembers().stream()
               .anyMatch(member -> member.getId() == currentUser.getId());

       if (isMember) {
           redirectAttributes.addFlashAttribute("errorMessage", "You are already a member of this Group.");
       } else {
           subSubGroup.addMember(currentUser);
           subSubGroupService.saveSubSubGroup(subSubGroup);
           redirectAttributes.addFlashAttribute("successMessage", "You have successfully joined the Group.");
           // redirectAttributes.addFlashAttribute("isMember", true);
       }

       redirectAttributes.addFlashAttribute("isMember", true);
       return "redirect:/subsubgroup?subsubgroupId=" + subSubGroupId;
   }

   /**
    * Allows a user to leave a specific sub-subgroup.
    * 
    * Handles the process of a user leaving a sub-subgroup, including special 
    * handling for the group owner to transfer ownership.
    * 
    * @param subSubGroupId The ID of the sub-subgroup to leave
    * @param principal The currently authenticated user attempting to leave
    * @param redirectAttributes Attributes for passing success or error messages
    * @return Redirects to the parent subgroup page with updated membership status
    */
   
   @PostMapping("/leaveSubSubGroup")
   @Transactional
   public String leaveSubSubGroup(@RequestParam Long subSubGroupId, Principal principal, RedirectAttributes redirectAttributes) {
       SubSubGroup subSubGroup = subSubGroupService.getSubSubGroupById(subSubGroupId);
       User currentUser = userService.getUserByUsername(principal.getName());

       if (subSubGroup == null || currentUser == null) {
           redirectAttributes.addFlashAttribute("errorMessage", "Group or user not found.");
           return "redirect:/subgroup?subgroupId=" + subSubGroup.getSubGroup().getId();
       }

       boolean isMember = subSubGroup.getMembers().stream()
               .anyMatch(member -> member.getId() == currentUser.getId());

       if (!isMember) {
           redirectAttributes.addFlashAttribute("errorMessage", "You are not a member of this Group.");
       } else {
           if (subSubGroup.getOwner().equals(currentUser)) {
               if (!subSubGroup.getMembers().isEmpty()) {
                   User newOwner = subSubGroup.getMembers().stream()
                           .filter(member -> !member.equals(currentUser))
                           .findFirst()
                           .orElse(null);

                   if (newOwner != null) {
                       subSubGroup.setOwner(newOwner);
                       redirectAttributes.addFlashAttribute("successMessage", "Ownership transferred to " + newOwner.getUsername() + ".");
                   } else {
                       redirectAttributes.addFlashAttribute("errorMessage", "You are the last member. Cannot transfer ownership.");
                   }
               } else {
                   redirectAttributes.addFlashAttribute("errorMessage", "You are the last member. Cannot transfer ownership.");
               }
           }

           
           subSubGroup.removeMember(currentUser);
           subSubGroupService.saveSubSubGroup(subSubGroup);

           
           if (subSubGroup.getMembers().isEmpty()) {
              
               redirectAttributes.addFlashAttribute("successMessage", "You have successfully left the Group. The Group has been deleted.");
               subSubGroupService.deleteSubSubGroup(subSubGroup);
           } else {
               redirectAttributes.addFlashAttribute("successMessage", "You have successfully left the Group.");
           }
       }

       redirectAttributes.addFlashAttribute("isMember", false);
       return "redirect:/subgroup?subgroupId=" + subSubGroup.getSubGroup().getId();
   }
   
   /**
    * Adds a comment to a specific post in a car club subgroup.
    * 
    * Allows a user to add a comment to an existing post in a subgroup.
    * 
    * @param content The text content of the comment
    * @param postId The ID of the post being commented on
    * @param user The user adding the comment (from authentication)
    * @param model The Spring MVC model for passing data to the view
    * @param principal The currently authenticated user
    * @return Redirects to the subgroup page
    */

   @PostMapping("/addComment")
   public String addComment(@RequestParam String content, 
                            @RequestParam Long postId, 
                            @AuthenticationPrincipal User user, Model model, Principal principal) {
       ClubPosts carClubPost = carClubPostsService.getCarClubPostById(postId);  
       model.addAttribute("carClubPost", carClubPost); 
       
       User currentUser = userService.getUserByUsername(principal.getName());
       carClubPost.setUser(currentUser);
       
       carClubCommentsService.saveComment(content, postId, currentUser);
       
       return "redirect:/subgroup?subgroupId=" + carClubPost.getSubgroupId();

   }
   
   /**
    * Creates a new post in a sub-subgroup.
    * 
    * Allows a user to create and submit a post in a specific sub-subgroup.
    * 
    * @param subSubGroupPost The post to be created
    * @param subsubgroupId The ID of the sub-subgroup where the post is being created
    * @param principal The currently authenticated user creating the post
    * @param model The Spring MVC model for passing data to the view
    * @return Redirects to the sub-subgroup page
    */
   
   @PostMapping("/subSubGroupPosts")  
   public String createSubSubGroupPost(@ModelAttribute SubSubGroupPosts subSubGroupPost, @RequestParam("subsubgroupId") Long subsubgroupId, Principal principal, Model model) {  
      subSubGroupPost.setSubSubgroupId(subsubgroupId);  
      User currentUser = userService.getUserByUsername(principal.getName());  
      subSubGroupPost.setUser(currentUser);  
      subSubGroupPostsService.createSubSubGroupPost(subSubGroupPost);  
      model.addAttribute("currentUser", currentUser);  
      return "redirect:/subsubgroup?subsubgroupId=" + subSubGroupPost.getSubSubgroupId();  
      
   }

   /**
    * Retrieves a specific sub-subgroup post by its ID.
    * 
    * @param id The unique identifier of the post
    * @param model The Spring MVC model for passing data to the view
    * @return Redirects to the sub-subgroup page containing the post
    */
   
   @GetMapping("/subSubGroupPosts/{id}")
   public String getSubSubGroupPostById(@PathVariable Long id, Model model) {
       
       SubSubGroupPosts subSubGroupPost = subSubGroupPostsService.getPostById(id)
               .orElseThrow(() -> new RuntimeException("Post not found for id :: " + id));
       
       model.addAttribute("subSubGroupPost", subSubGroupPost);
       model.addAttribute("comments", subCommentService.getCommentsByPostId(id));
       
       return "redirect:/subsubgroup?subsubgroupId=" + subSubGroupPost.getSubSubgroupId();
   }
   
   /**
    * Adds a comment to a specific post in a sub-subgroup.
    * 
    * Allows a user to add a comment to an existing post in a sub-subgroup.
    * 
    * @param content The text content of the comment
    * @param postId The ID of the post being commented on
    * @param user The user adding the comment (from authentication)
    * @param model The Spring MVC model for passing data to the view
    * @param principal The currently authenticated user
    * @return Redirects to the sub-subgroup page
    */
   
   @PostMapping("/addSubComment")
   public String addSubComment(@RequestParam String content,
                               @RequestParam Long postId,
                               @AuthenticationPrincipal User user,
                               Model model,
                               Principal principal) {
       
      
       SubSubGroupPosts subSubGroupPost = subSubGroupPostsService.getPostById(postId)
               .orElseThrow(() -> new RuntimeException("Post not found for id :: " + postId));

       model.addAttribute("subSubGroupPost", subSubGroupPost);

       User currentUser = userService.getUserByUsername(principal.getName());

       SubComment subComment = new SubComment();
       subComment.setContent(content);
       subComment.setSubSubGroupPost(subSubGroupPost);
       subComment.setUser(currentUser);

       subCommentService.createComment(subComment);

       return "redirect:/subsubgroup?subsubgroupId=" + subSubGroupPost.getSubSubgroupId();
   }
   

}
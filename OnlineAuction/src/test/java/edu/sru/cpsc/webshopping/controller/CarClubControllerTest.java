
package edu.sru.cpsc.webshopping.controller;  
  
import static org.junit.Assert.assertNull;  
import static org.junit.Assert.assertEquals;  
import static org.junit.Assert.assertSame;  
  
import org.junit.Test;  
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;  
import org.mockito.Mock;  
import org.mockito.junit.MockitoJUnitRunner;  
  
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
import edu.sru.cpsc.webshopping.service.UserService;  
  
public class CarClubControllerTest {  
  
   @Mock  
   private ClubsService carClubService;  
  
   @Mock  
   private SubGroupService subgroupService;  
  
   @Mock  
   private ClubsRepository clubsRepository;  
  
   @Mock  
   private SubGroupRepository subgroupRepository;  
  
   @Mock  
   private UserService userService;  
  
   @Mock  
   private ClubPostsService carClubPostsService;  
  
   @Mock  
   private CarClubCommentsService carClubCommentsService;  
  
   @Mock  
   private ClubCommentsRepository clubCommentsRepository;  
  
   @Mock  
   private SubSubGroupService subSubGroupService;  
  
   @Mock  
   private SubSubGroupPostsService subSubGroupPostsService;  
  
   @Mock  
   private SubCommentService subCommentService;  
  
   @InjectMocks  
   private CarClubController carClubController;  
  

  
   @Test  
   public void testShowCarClub() {  
      Clubs clubs = new Clubs();  
      assertEquals(carClubController.showCarClub(1L, null, null), "redirect:/Social");  
   }  
  
   @Test  
   public void testCreateSubGroup() {  
      SubGroup subGroup = new SubGroup();  
      assertEquals(carClubController.createSubGroup(1L, "Test Sub Group Name", "Test Sub Group Description", null, null, null), "redirect:/carClub?clubsId=1");  
   }  
  
   @Test  
   public void testJoinSubGroup() {  
      assertEquals(carClubController.joinSubGroup(1L, null, null), "redirect:/subgroup?subgroupId=1");  
   }  
  
   @Test  
   public void testLeaveSubGroup() {  
      assertEquals(carClubController.leaveSubGroup(1L, null, null), "redirect:/carClub?clubsId=1");  
   }  
  
   @Test  
   public void testCreateSubSubGroup() {  
      assertEquals(carClubController.createSubSubGroup(1L, "Test Sub Sub Group Name", "Test Sub Sub Group Description", null, null), "redirect:/subgroup?subgroupId=1");  
   }  
  
   @Test  
   public void testJoinSubSubGroup() {  
      assertEquals(carClubController.joinSubSubGroup(1L, null, null), "redirect:/subsubgroup?subsubgroupId=1");  
   }  
  
   @Test  
   public void testLeaveSubSubGroup() {  
      assertEquals(carClubController.leaveSubSubGroup(1L, null, null), "redirect:/subgroup?subgroupId=1");  
   }  
  
   @Test  
   public void testLeavePostForSubGroup() {  
      ClubPosts clubPosts = new ClubPosts();  
      assertEquals(carClubController.getAllCarClubPosts(clubPosts, null, null), "redirect:/subgroup?subgroupId=1");  
   }  
  
   @Test  
   public void testLeavePostForSubSubGroup() {  
      SubSubGroupPosts subSubGroupPosts = new SubSubGroupPosts();  
      assertEquals(carClubController.createSubSubGroupPost(subSubGroupPosts, 1L, null, null), "redirect:/subsubgroup?subsubgroupId=1");  
   }  
  
   @Test  
   public void testAddComment() {  
      assertEquals(carClubController.addComment("Test Comment", 1L, null, null, null), "redirect:/subgroup?subgroupId=1");  
   }  
  
   @Test  
   public void testAddSubComment() {  
      assertEquals(carClubController.addSubComment("Test Sub Comment", 1L, null, null, null), "redirect:/subsubgroup?subsubgroupId=1");  
   }  
}
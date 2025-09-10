package edu.sru.cpsc.webshopping.domain.group;

import edu.sru.cpsc.webshopping.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class SubGroupTest {

    private SubGroup subGroup;
    private User owner;
    private Clubs club;

    @BeforeEach
    public void setUp() {
        owner = new User();
        club = new Clubs("Test Club", "Club Description", LocalDateTime.now(), "http://example.com/image.jpg");
        subGroup = new SubGroup("Test SubGroup", "SubGroup Description", club, owner, LocalDateTime.now());
    }

    @Test
    public void testSubGroupAttributes() {
        subGroup.setId(1L);
        assertEquals(1L, subGroup.getId());
        subGroup.setName("New SubGroup");
        assertEquals("New SubGroup", subGroup.getName());
        subGroup.setDescription("Updated description");
        assertEquals("Updated description", subGroup.getDescription());
    }

    @Test
    public void testAddAndRemoveMember() {
        User member = new User();
        subGroup.addMember(member);
        assertTrue(subGroup.isMember(member));
        subGroup.removeMember(member);
        assertFalse(subGroup.isMember(member));
    }

    @Test
    public void testCreateSubSubGroup() {
        SubSubGroup newSubSubGroup = subGroup.createSubSubGroup("New SubSubGroup", "Description", owner);
        assertNotNull(newSubSubGroup);
        assertTrue(subGroup.getSubSubGroups().contains(newSubSubGroup));
    }

    @Test
    public void testSubGroupEquality() {
        SubGroup anotherSubGroup = new SubGroup("Test SubGroup", "Description", club, owner, LocalDateTime.now());
        anotherSubGroup.setId(1L);
        subGroup.setId(1L);
        assertEquals(subGroup, anotherSubGroup);
        assertNotEquals(subGroup, new SubGroup());
    }

    @Test
    public void testToString() {
        subGroup.setId(1L);
        String expectedString = "Subgroup{id=1, name='Test SubGroup', description='SubGroup Description', carClub=" + club.getId() + ", owner=" + owner.getId() + ", creationDate=" + subGroup.getCreationDate() + ", membersCount=0}";
        assertTrue(subGroup.toString().contains("Test SubGroup"));
    }

    @Test
    public void testSetAndGetMembers() {
        Set<User> members = new HashSet<>();
        User newUser = new User();
        members.add(newUser);
        subGroup.setMembers(members);
        assertTrue(subGroup.getMembers().contains(newUser));
    }

}
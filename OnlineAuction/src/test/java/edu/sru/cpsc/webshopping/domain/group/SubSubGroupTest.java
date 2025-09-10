package edu.sru.cpsc.webshopping.domain.group;

import edu.sru.cpsc.webshopping.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SubSubGroupTest {

    private SubSubGroup subSubGroup;
    private User user;
    private SubGroup subGroup;
    private Clubs clubs;

    @BeforeEach
    public void setUp() {
        user = new User();
        subGroup = new SubGroup();
        clubs = new Clubs();
        subSubGroup = new SubSubGroup("Test SubSubGroup", "Test Description", subGroup, clubs, user, LocalDateTime.now());
    }

    @Test
    public void testSubSubGroupAttributes() {
        subSubGroup.setId(1L);
        assertEquals(1L, subSubGroup.getId());
        subSubGroup.setName("Test SubSubGroup");
        assertEquals("Test SubSubGroup", subSubGroup.getName());
        subSubGroup.setDescription("Test Description");
        assertEquals("Test Description", subSubGroup.getDescription());
    }

    @Test
    public void testSubSubGroupRelationships() {
        subSubGroup.setSubGroup(subGroup);
        assertEquals(subGroup, subSubGroup.getSubGroup());
        subSubGroup.setClubs(clubs);
        assertEquals(clubs, subSubGroup.getClubs());
        subSubGroup.setOwner(user);
        assertEquals(user, subSubGroup.getOwner());
    }

    @Test
    public void testSetAndGetMembers() {
        Set<User> members = new HashSet<>();
        members.add(user);
        subSubGroup.setMembers(members);
        assertTrue(subSubGroup.getMembers().contains(user));
    }

    @Test
    public void testAddAndRemoveMember() {
        subSubGroup.addMember(user);
        assertTrue(subSubGroup.isMember(user));
        subSubGroup.removeMember(user);
        assertFalse(subSubGroup.isMember(user));
    }

    @Test
    public void testSubSubGroupEquality() {
        SubSubGroup anotherSubSubGroup = new SubSubGroup("Test SubSubGroup", "Test Description", subGroup, clubs, user, LocalDateTime.now());
        anotherSubSubGroup.setId(1L);
        subSubGroup.setId(1L);
        assertEquals(subSubGroup, anotherSubSubGroup);
        assertNotEquals(subSubGroup, new SubSubGroup());
    }

    @Test
    public void testToString() {
        subSubGroup.setId(1L);
        subSubGroup.setName("Test SubSubGroup");
        subSubGroup.setDescription("Test Description");
        String expectedString = "SubSubGroup{id=1, name='Test SubSubGroup', description='Test Description'}";
        assertTrue(subSubGroup.toString().contains("Test SubSubGroup"));
    }
}
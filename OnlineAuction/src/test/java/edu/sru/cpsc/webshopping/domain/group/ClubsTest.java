package edu.sru.cpsc.webshopping.domain.group;

import edu.sru.cpsc.webshopping.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ClubsTest {

    private Clubs clubs;
    private User user;
    private SubGroup subGroup;

    @BeforeEach
    public void setUp() {
        clubs = new Clubs("Test Club", "Description of the club", LocalDateTime.now(), "http://example.com/image.jpg");
        user = new User();
        subGroup = new SubGroup("Test SubGroup", "Description of SubGroup", clubs, user, LocalDateTime.now());
    }

    @Test
    public void testClubAttributes() {
        clubs.setId(1L);
        assertEquals(1L, clubs.getId());
        assertEquals("Test Club", clubs.getClubName());
        assertEquals("Description of the club", clubs.getGroupDescription());
        assertNotNull(clubs.getCreationDate());
        assertEquals("http://example.com/image.jpg", clubs.getImageUrl());
    }

    @Test
    public void testAddAndRemoveSubGroup() {
        clubs.addSubGroup(subGroup);
        assertTrue(clubs.getSubGroups().contains(subGroup));
        clubs.removeSubGroup(subGroup);
        assertFalse(clubs.getSubGroups().contains(subGroup));
    }

    @Test
    public void testCreateSubGroup() {
        SubGroup newSubGroup = clubs.createSubGroup("New SubGroup", "New SubGroup Description", user);
        assertNotNull(newSubGroup);
        assertTrue(clubs.getSubGroups().contains(newSubGroup));
    }

    @Test
    public void testClubEquality() {
        Clubs anotherClub = new Clubs("Another Club", "Another Description", LocalDateTime.now(), "http://example.com/another-image.jpg");
        anotherClub.setId(1L);
        clubs.setId(1L);
        assertEquals(clubs, anotherClub);
        assertNotEquals(clubs, new Clubs());
    }

    @Test
    public void testToString() {
        String expectedString = "CarClub{id=1, carBrand='Test Club', groupDescription='Description of the club', creationDate=" + clubs.getCreationDate() + ", imageUrl='http://example.com/image.jpg', membersCount=0, SubGroupsCount=0}";
        clubs.setId(1L);
        assertTrue(clubs.toString().contains("Test Club"));
    }

    @Test
    public void testSetAndGetSubGroups() {
        Set<SubGroup> subGroups = new HashSet<>();
        subGroups.add(subGroup);
        clubs.setSubGroups(subGroups);
        assertTrue(clubs.getSubGroups().contains(subGroup));
    }


    @Test
    public void testSubGroupCreation() {
        SubGroup createdSubGroup = clubs.createSubGroup("SubGroup 1", "Description", user);
        assertNotNull(createdSubGroup);
        assertEquals("SubGroup 1", createdSubGroup.getName());
    }
}
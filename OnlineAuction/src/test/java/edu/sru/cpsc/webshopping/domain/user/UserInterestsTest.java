package edu.sru.cpsc.webshopping.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserInterestsTest {

    private UserInterests userInterests;
    private User testUser;

    @BeforeEach
    public void setUp() {
        userInterests = new UserInterests();
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    public void testSetAndGetId() {
        Long testId = 1L;
        userInterests.setId(testId);
        assertEquals(testId, userInterests.getId());
    }

    @Test
    public void testSetAndGetUser() {
        userInterests.setUser(testUser);
        assertEquals(testUser, userInterests.getUser());
    }

    @Test
    public void testSetAndGetHome() {
        userInterests.setHome(true);
        assertTrue(userInterests.getHome());

        userInterests.setHome(false);
        assertFalse(userInterests.getHome());
    }

    @Test
    public void testSetAndGetAuto() {
        userInterests.setAuto(true);
        assertTrue(userInterests.getAuto());

        userInterests.setAuto(false);
        assertFalse(userInterests.getAuto());
    }

    @Test
    public void testSetAndGetClothing() {
        userInterests.setClothing(true);
        assertTrue(userInterests.getClothing());

        userInterests.setClothing(false);
        assertFalse(userInterests.getClothing());
    }

    @Test
    public void testSetAndGetSports() {
        userInterests.setSports(true);
        assertTrue(userInterests.getSports());

        userInterests.setSports(false);
        assertFalse(userInterests.getSports());
    }

    @Test
    public void testSetAndGetArt() {
        userInterests.setArt(true);
        assertTrue(userInterests.getArt());

        userInterests.setArt(false);
        assertFalse(userInterests.getArt());
    }

    @Test
    public void testSetAndGetCosmetics() {
        userInterests.setCosmetics(true);
        assertTrue(userInterests.getCosmetics());

        userInterests.setCosmetics(false);
        assertFalse(userInterests.getCosmetics());
    }

    @Test
    public void testAll() {
        userInterests.setId(1L);
        userInterests.setUser(testUser);
        userInterests.setHome(true);
        userInterests.setAuto(false);
        userInterests.setClothing(true);
        userInterests.setSports(false);
        userInterests.setArt(true);
        userInterests.setCosmetics(false);

        assertEquals(1L, userInterests.getId());
        assertEquals(testUser, userInterests.getUser());
        assertTrue(userInterests.getHome());
        assertFalse(userInterests.getAuto());
        assertTrue(userInterests.getClothing());
        assertFalse(userInterests.getSports());
        assertTrue(userInterests.getArt());
        assertFalse(userInterests.getCosmetics());
    }
}

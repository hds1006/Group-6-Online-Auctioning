package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.group.Clubs;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.group.ClubsRepository;
import edu.sru.cpsc.webshopping.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClubsServiceTest {

    @Mock
    private ClubsRepository clubsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClubsService clubsService;

    private User user;
    private Clubs clubs;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        clubs = new Clubs();
        clubs.setId(1L);
        clubs.setClubName("Test Club");
        clubs.setMembers(new HashSet<>());
    }

    @Test
    public void testGetAllCarClubsForUser() {
        Set<Clubs> clubsSet = new HashSet<>();
        clubsSet.add(clubs);

        when(clubsRepository.findByMembersContaining(user)).thenReturn(List.of(clubs));

        List<Clubs> result = clubsService.getAllCarClubsForUser(user);

        assertEquals(1, result.size());
        assertTrue(result.contains(clubs));
        verify(clubsRepository, times(1)).findByMembersContaining(user);
    }

    @Test
    public void testGetClubsById() {
        when(clubsRepository.findById(1L)).thenReturn(Optional.of(clubs));

        Clubs result = clubsService.getClubsById(1L);

        assertNotNull(result);
        assertEquals(clubs.getId(), result.getId());
        verify(clubsRepository, times(1)).findById(1L);
    }

 

    @Test
    public void testAddMemberToClub() {
        when(clubsRepository.findById(1L)).thenReturn(Optional.of(clubs));

        boolean result = clubsService.addMemberToClub(user, clubs);

        assertTrue(result);
        verify(clubsRepository, times(1)).save(clubs);
    }

    @Test
    public void testSaveCarClub() {
        when(clubsRepository.save(clubs)).thenReturn(clubs);

        Clubs result = clubsService.saveCarClub(clubs);

        assertNotNull(result);
        verify(clubsRepository, times(1)).save(clubs);
    }

    

    @Test
    public void testJoinCarClub() {
        when(clubsRepository.findById(1L)).thenReturn(Optional.of(clubs));

        clubsService.joinCarClub(1L, user);

        assertTrue(clubs.getMembers().contains(user));
        verify(clubsRepository, times(1)).save(clubs);
    }

    @Test
    public void testLeaveCarClub() {
        when(clubsRepository.findById(1L)).thenReturn(Optional.of(clubs));
        clubs.getMembers().add(user);

        clubsService.leaveCarClub(1L, user);

        assertFalse(clubs.getMembers().contains(user));
        verify(clubsRepository, times(1)).save(clubs);
    }

    @Test
    public void testIsUserMember() {
        when(clubsRepository.findById(1L)).thenReturn(Optional.of(clubs));

        clubs.getMembers().add(user);

        boolean result = clubsService.isUserMember(1L, user);

        assertTrue(result);
        verify(clubsRepository, times(1)).findById(1L);
    }
}



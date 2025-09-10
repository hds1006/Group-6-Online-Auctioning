package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.group.SubSubGroup;
import edu.sru.cpsc.webshopping.domain.group.SubGroup;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.group.SubSubGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubSubGroupServiceTest {

    @Mock
    private SubSubGroupRepository subSubGroupRepository;

    @InjectMocks
    private SubSubGroupService subSubGroupService;

    private SubSubGroup subSubGroup;
    private SubGroup subGroup;
    private User owner;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        subGroup = new SubGroup();
        subGroup.setId(1L);
        owner = new User();
        owner.setId(1L);
        
        subSubGroup = new SubSubGroup(
            "Test Sub-Sub Group", 
            "Test Description", 
            subGroup, 
            subGroup.getClubs(), 
            owner, 
            LocalDateTime.now()
        );
    }

  

    @Test
    public void testGetSubSubGroupById() {
        Long subSubGroupId = 1L;
        when(subSubGroupRepository.findById(subSubGroupId)).thenReturn(Optional.of(subSubGroup));

        SubSubGroup result = subSubGroupService.getSubSubGroupById(subSubGroupId);

        assertNotNull(result);
        assertEquals(subSubGroup, result);
        verify(subSubGroupRepository, times(1)).findById(subSubGroupId);
    }

    @Test
    public void testGetSubSubGroupByIdNotFound() {
        Long subSubGroupId = 1L;
        when(subSubGroupRepository.findById(subSubGroupId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> subSubGroupService.getSubSubGroupById(subSubGroupId));
    }

    @Test
    public void testGetSubSubGroupsBySubGroup() {
        Long subGroupId = 1L;
        when(subSubGroupRepository.findBySubGroupId(subGroupId)).thenReturn(List.of(subSubGroup));

        List<SubSubGroup> result = subSubGroupService.getSubSubGroupsBySubGroup(subGroupId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(subSubGroup));
        verify(subSubGroupRepository, times(1)).findBySubGroupId(subGroupId);
    }

    @Test
    public void testSaveSubSubGroup() {
        when(subSubGroupRepository.save(subSubGroup)).thenReturn(subSubGroup);

        subSubGroupService.saveSubSubGroup(subSubGroup);

        verify(subSubGroupRepository, times(1)).save(subSubGroup);
    }

    @Test
    public void testDeleteSubSubGroup() {
        subSubGroupService.deleteSubSubGroup(subSubGroup);

        verify(subSubGroupRepository, times(1)).delete(subSubGroup);
    }

    @Test
    public void testSearchSubSubGroups() {
        String searchQuery = "Test";
        when(subSubGroupRepository.findByNameContainingIgnoreCase(searchQuery)).thenReturn(List.of(subSubGroup));

        List<SubSubGroup> result = subSubGroupService.searchSubSubGroups(searchQuery);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(subSubGroup));
        verify(subSubGroupRepository, times(1)).findByNameContainingIgnoreCase(searchQuery);
    }

    @Test
    public void testGetAllSubSubGroups() {
        when(subSubGroupRepository.findAll()).thenReturn(List.of(subSubGroup));

        List<SubSubGroup> result = subSubGroupService.getAllSubSubGroups();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(subSubGroup));
        verify(subSubGroupRepository, times(1)).findAll();
    }
}
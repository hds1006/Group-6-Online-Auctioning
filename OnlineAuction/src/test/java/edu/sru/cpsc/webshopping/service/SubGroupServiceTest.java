package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.group.SubGroup;
import edu.sru.cpsc.webshopping.repository.group.SubGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubGroupServiceTest {

    @Mock
    private SubGroupRepository subgroupRepository;

    @InjectMocks
    private SubGroupService subGroupService;

    private SubGroup subgroup;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        subgroup = new SubGroup();
        subgroup.setId(1L);
        subgroup.setName("Test SubGroup");
    }

    @Test
    public void testGetSubgroupsByCarClub() {
        Long clubsId = 1L;
        when(subgroupRepository.findByClubsId(clubsId)).thenReturn(List.of(subgroup));

        List<SubGroup> result = subGroupService.getSubgroupsByCarClub(clubsId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(subgroup));
        verify(subgroupRepository, times(1)).findByClubsId(clubsId);
    }

    @Test
    public void testGetSubGroupById() {
        Long subgroupId = 1L;
        when(subgroupRepository.findById(subgroupId)).thenReturn(Optional.of(subgroup));

        SubGroup result = subGroupService.getSubGroupById(subgroupId);

        assertNotNull(result);
        assertEquals(subgroupId, result.getId());
        verify(subgroupRepository, times(1)).findById(subgroupId);
    }

    @Test
    public void testSaveSubGroup() {
        when(subgroupRepository.save(subgroup)).thenReturn(subgroup);

        SubGroup result = subGroupService.saveSubGroup(subgroup);

        assertNotNull(result);
        assertEquals(subgroup, result);
        verify(subgroupRepository, times(1)).save(subgroup);
    }

    @Test
    public void testSearchSubGroups() {
        String query = "Test";
        when(subgroupRepository.findByNameContainingIgnoreCase(query)).thenReturn(List.of(subgroup));

        List<SubGroup> result = subGroupService.searchSubGroups(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(subgroup));
        verify(subgroupRepository, times(1)).findByNameContainingIgnoreCase(query);
    }

    @Test
    public void testGetAllSubGroups() {
        when(subgroupRepository.findAll()).thenReturn(List.of(subgroup));

        List<SubGroup> result = subGroupService.getAllSubGroups();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(subgroup));
        verify(subgroupRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteSubGroup() {
        subGroupService.deleteSubGroup(subgroup);

        verify(subgroupRepository, times(1)).delete(subgroup);
    }
}
package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.group.SubSubGroupPosts;
import edu.sru.cpsc.webshopping.repository.group.SubSubGroupPostsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubSubGroupPostsServiceTest {

    @Mock
    private SubSubGroupPostsRepository subSubGroupPostsRepository;

    @InjectMocks
    private SubSubGroupPostsService subSubGroupPostsService;

    private SubSubGroupPosts subSubGroupPost;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        subSubGroupPost = new SubSubGroupPosts();
        subSubGroupPost.setId(1L);
        subSubGroupPost.setTitle("Test Post");
        subSubGroupPost.setContent("Test Content");
    }

    @Test
    public void testCreateSubSubGroupPost() {
        when(subSubGroupPostsRepository.save(subSubGroupPost)).thenReturn(subSubGroupPost);

        subSubGroupPostsService.createSubSubGroupPost(subSubGroupPost);

        verify(subSubGroupPostsRepository, times(1)).save(subSubGroupPost);
    }

    @Test
    public void testGetPostById() {
        Long postId = 1L;
        when(subSubGroupPostsRepository.findById(postId)).thenReturn(Optional.of(subSubGroupPost));

        Optional<SubSubGroupPosts> result = subSubGroupPostsService.getPostById(postId);

        assertTrue(result.isPresent());
        assertEquals(subSubGroupPost, result.get());
        verify(subSubGroupPostsRepository, times(1)).findById(postId);
    }

    @Test
    public void testGetPostsBySubsubgroupId() {
        Long subsubgroupId = 1L;
        when(subSubGroupPostsRepository.findBySubsubgroupId(subsubgroupId)).thenReturn(List.of(subSubGroupPost));

        List<SubSubGroupPosts> result = subSubGroupPostsService.getPostsBySubsubgroupId(subsubgroupId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(subSubGroupPost));
        verify(subSubGroupPostsRepository, times(1)).findBySubsubgroupId(subsubgroupId);
    }

    @Test
    public void testGetPostsByUserId() {
        Long userId = 1L;
        when(subSubGroupPostsRepository.findByUserId(userId)).thenReturn(List.of(subSubGroupPost));

        List<SubSubGroupPosts> result = subSubGroupPostsService.getPostsByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(subSubGroupPost));
        verify(subSubGroupPostsRepository, times(1)).findByUserId(userId);
    }
}
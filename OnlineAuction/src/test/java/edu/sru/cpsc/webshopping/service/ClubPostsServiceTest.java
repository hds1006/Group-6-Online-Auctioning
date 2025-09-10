package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.group.ClubPosts;
import edu.sru.cpsc.webshopping.repository.group.ClubPostsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClubPostsServiceTest {

    private ClubPostsService clubPostsService;
    private ClubPostsRepository clubPostsRepository;

    @BeforeEach
    public void setUp() {
        clubPostsRepository = mock(ClubPostsRepository.class);
        clubPostsService = new ClubPostsService(clubPostsRepository);
    }

    @Test
    public void testGetAllCarClubPosts() {
        ClubPosts post1 = new ClubPosts();
        ClubPosts post2 = new ClubPosts();
        List<ClubPosts> posts = Arrays.asList(post1, post2);

        when(clubPostsRepository.findAll()).thenReturn(posts);

        List<ClubPosts> result = clubPostsService.getAllCarClubPosts();

        assertEquals(2, result.size());
        verify(clubPostsRepository, times(1)).findAll();
    }

    @Test
    public void testGetCarClubPostById() {
        Long postId = 1L;
        ClubPosts post = new ClubPosts();

        when(clubPostsRepository.findById(postId)).thenReturn(Optional.of(post));

        ClubPosts result = clubPostsService.getCarClubPostById(postId);

        assertNotNull(result);
        verify(clubPostsRepository, times(1)).findById(postId);
    }

    @Test
    public void testCreateCarClubPost() {
        ClubPosts post = new ClubPosts();

        when(clubPostsRepository.save(post)).thenReturn(post);

        ClubPosts result = clubPostsService.createCarClubPost(post);

        assertNotNull(result);
        verify(clubPostsRepository, times(1)).save(post);
    }

    @Test
    public void testFindBySubgroupId() {
        Long subgroupId = 1L;
        ClubPosts post1 = new ClubPosts();
        ClubPosts post2 = new ClubPosts();
        List<ClubPosts> posts = Arrays.asList(post1, post2);

        when(clubPostsRepository.findBySubgroupId(subgroupId)).thenReturn(posts);

        List<ClubPosts> result = clubPostsService.findBySubgroupId(subgroupId);

        assertEquals(2, result.size());
        verify(clubPostsRepository, times(1)).findBySubgroupId(subgroupId);
    }
}
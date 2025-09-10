package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.group.SubComment;
import edu.sru.cpsc.webshopping.repository.group.SubCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubCommentServiceTest {

    @Mock
    private SubCommentRepository subCommentRepository;

    @InjectMocks
    private SubCommentService subCommentService;

    private SubComment subComment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        subComment = new SubComment();
        subComment.setId(1L);
        subComment.setContent("Test comment");
    }

    @Test
    public void testCreateComment() {
        when(subCommentRepository.save(subComment)).thenReturn(subComment);

        subCommentService.createComment(subComment);

        verify(subCommentRepository, times(1)).save(subComment);
    }

    @Test
    public void testGetCommentsByPostId() {
        Long postId = 1L;
        when(subCommentRepository.findBySubSubGroupPostId(postId)).thenReturn(List.of(subComment));

        List<SubComment> result = subCommentService.getCommentsByPostId(postId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(subComment));
        verify(subCommentRepository, times(1)).findBySubSubGroupPostId(postId);
    }
}
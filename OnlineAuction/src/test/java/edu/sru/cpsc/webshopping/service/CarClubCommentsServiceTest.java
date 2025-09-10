package edu.sru.cpsc.webshopping.service;

import edu.sru.cpsc.webshopping.domain.group.ClubComments;
import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.group.ClubCommentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class CarClubCommentsServiceTest {
    private CarClubCommentsService carClubCommentsService;
    private ClubCommentsRepository clubCommentsRepository;
    private User user;

    @BeforeEach
    public void setUp() {
        clubCommentsRepository = mock(ClubCommentsRepository.class);
        carClubCommentsService = new CarClubCommentsService(clubCommentsRepository);
        user = new User();
        user.setId(1L);
    }

    @Test
    public void testSaveComment() {
        // Arrange
        String content = "This is a comment";
        Long postId = 1L;

        // Act
        carClubCommentsService.saveComment(content, postId, user);

        // Assert
        verify(clubCommentsRepository, times(1)).save(argThat(comment -> 
            comment.getContent().equals(content) &&
            comment.getPostId().equals(postId) &&
            comment.getUser().equals(user)
        ));
    }
}
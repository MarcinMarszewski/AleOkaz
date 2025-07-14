package pl.aleokaz.backend.post;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.aleokaz.backend.comment.Comment;
import pl.aleokaz.backend.comment.CommentRepository;
import pl.aleokaz.backend.comment.CommentService;
import pl.aleokaz.backend.interaction.InteractionService;
import pl.aleokaz.backend.user.User;
import pl.aleokaz.backend.user.UserService;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private InteractionService interactionService;

    @Spy
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Test
    public void shouldCreateComment() throws Exception {
        final var author = User.builder()
                .id(UUID.randomUUID())
                .username("user")
                .email("user@example.com")
                .password("")
                .roles(new HashSet<>())
                .profilePicture("empty")
                .build();
        final var post = Post.builder()
                .id(UUID.randomUUID())
                .content("Lorem ipsum dolor sit amet")
                .imageUrl("https://example.com/image.jpg")
                .createdAt(new Date())
                .editedAt(new Date())
                .author(author)
                .reactions(new HashSet<>())
                .build();

        when(userService.getUserById(author.id()))
                .thenReturn(author);
        when(interactionService.getInteractionById(post.id()))
                .thenReturn(post);
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    final Comment comment = invocation.getArgument(0);
                    comment.id(UUID.randomUUID());
                    return comment;
                });
        final var result = commentService.createComment(author.id(), post.id(), "More dolor sit amet");

        assertEquals("More dolor sit amet", result.content());
    }
}

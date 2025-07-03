package pl.aleokaz.backend.post;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.aleokaz.backend.reaction.ReactionCommand;
import pl.aleokaz.backend.reaction.ReactionService;
import pl.aleokaz.backend.reaction.ReactionType;
import pl.aleokaz.backend.user.User;
import pl.aleokaz.backend.user.UserRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ReactionServiceTest {
    @InjectMocks
    private ReactionService reactionService;

    @Spy
    private InteractionMapper postMapper;

    @Mock
    private InteractionRepository interactionRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    public void shouldSetPostReaction() throws Exception {
        final var author = User.builder()
                .id(UUID.randomUUID())
                .username("user")
                .email("user@example.com")
                .password("")
                .roles(new HashSet<>())
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

        when(userRepository.findById(author.id()))
                .thenReturn(Optional.of(author));
        when(interactionRepository.findById(post.id()))
                .thenReturn(Optional.of(post));

        reactionService.setReaction(author.id(), new ReactionCommand(post.id(), ReactionType.LIKE));

        verify(interactionRepository).save(
                argThat(savedPost -> savedPost.reactions().stream()
                        .anyMatch(reaction -> reaction.type().equals(ReactionType.LIKE) &&
                                reaction.author().equals(author) &&
                                reaction.interaction().equals(post))));
    }
}

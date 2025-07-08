package pl.aleokaz.backend.comment;

import pl.aleokaz.backend.comment.commands.CreateCommentCommand;
import pl.aleokaz.backend.comment.commands.UpdateCommentCommand;
import pl.aleokaz.backend.comment.exceptions.CommentNotFoundException;
import pl.aleokaz.backend.interaction.InteractionRepository;
import pl.aleokaz.backend.security.AuthorizationException;
import pl.aleokaz.backend.user.UserNotFoundException;
import pl.aleokaz.backend.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.NonNull;

import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class CommentService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private InteractionRepository interactionRepository;

    public CommentDTO createComment(@NonNull UUID userId, @NonNull CreateCommentCommand command) {
        final var author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id", userId.toString()));
        final var parent = interactionRepository.findById(command.parentId())
                .orElseThrow(() -> new CommentNotFoundException("id", command.parentId().toString()));

        var comment = Comment.builder()
                .content(command.content())
                .createdAt(new Date())
                .author(author)
                .parent(parent)
                .build();
        comment = commentRepository.save(comment);

        return comment.asCommentDto();
    }

    public CommentDTO updateComment(@NonNull UUID userId, @NonNull UpdateCommentCommand command) {
        final var author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("id", userId.toString()));
        var comment = commentRepository.findById(command.commentId())
                .orElseThrow(() -> new CommentNotFoundException("id", command.commentId().toString()));

        if (!author.equals(comment.author())) {
            throw new AuthorizationException(userId.toString());
        }

        comment.content(command.content());
        comment.editedAt(new Date());
        comment = commentRepository.save(comment);

        return comment.asCommentDto();
    }

    public void deleteComment(@NonNull UUID userId, @NonNull UUID commentId) {
        final var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("id", commentId.toString()));

        if (!userId.equals(comment.author().id())) {
            throw new AuthorizationException(userId.toString());
        }

        comment.author(null);
        comment.content("This comment has been deleted.");
        comment.editedAt(new Date());
        commentRepository.save(comment);
    }
}

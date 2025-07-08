package pl.aleokaz.backend.comment;

import pl.aleokaz.backend.comment.commands.CreateCommentCommand;
import pl.aleokaz.backend.comment.commands.UpdateCommentCommand;
import pl.aleokaz.backend.reaction.ReactionCommand;
import pl.aleokaz.backend.reaction.ReactionService;
import pl.aleokaz.backend.reaction.ReactionType;
import pl.aleokaz.backend.security.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

//TODO: Add error handling with @ControllerAdvice
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReactionService reactionService;

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(Authentication authentication, @RequestBody CreateCommentCommand command) {
        final var currentUserId = authenticationService.getCurrentUserId(authentication);
        return ResponseEntity.ok().body(commentService.createComment(currentUserId, command));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(Authentication authentication, @PathVariable UUID commentId, @RequestBody UpdateCommentCommand command) {
        final var currentUserId = authenticationService.getCurrentUserId(authentication);
        return ResponseEntity.ok().body(commentService.updateComment(currentUserId, command));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(Authentication authentication, @PathVariable UUID commentId) {
        final var currentUserId = authenticationService.getCurrentUserId(authentication);
        commentService.deleteComment(currentUserId, commentId);
        return ResponseEntity.noContent().build();
    }
    
    //TODO: Move to reactions
    @PutMapping("/{commentId}/reactions")
    public ResponseEntity<Void> setPostReaction(
            Authentication authentication,
            @PathVariable UUID commentId) {
        final UUID userId = UUID.fromString((String) authentication.getPrincipal());

        // TODO: Wczytanie typu reakcji z @RequestBody.
        reactionService.setReaction(userId, new ReactionCommand(commentId, ReactionType.LIKE));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}/reactions")
    public ResponseEntity<Void> deletePostReaction(
            Authentication authentication,
            @PathVariable UUID commentId) {
        final UUID userId = UUID.fromString((String) authentication.getPrincipal());

        reactionService.deleteReaction(userId, commentId);

        return ResponseEntity.noContent().build();
    }
}

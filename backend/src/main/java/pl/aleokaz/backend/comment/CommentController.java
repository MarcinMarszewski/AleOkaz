package pl.aleokaz.backend.comment;

import pl.aleokaz.backend.comment.commands.CreateCommentCommand;
import pl.aleokaz.backend.comment.commands.UpdateCommentCommand;
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
}

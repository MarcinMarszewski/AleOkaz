package pl.aleokaz.backend.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import pl.aleokaz.backend.post.commands.PostCommand;
import pl.aleokaz.backend.reaction.ReactionCommand;
import pl.aleokaz.backend.reaction.ReactionService;
import pl.aleokaz.backend.reaction.ReactionType;
import pl.aleokaz.backend.security.AuthenticationService;

import java.util.List;
import java.util.UUID;

//TODO: @ControllerAdive
@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PostService postService;

    @Autowired
    private ReactionService reactionService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts(Authentication authentication, @RequestParam(name = "userId", required = false) UUID authorId) {
        UUID userId = authenticationService.getCurrentUserId(authentication);
        List<Post> posts = postService.getPostsByAuthorId(authorId == null ? userId : authorId);
        return new ResponseEntity<>(postService.postsAsPostDtos(posts), HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(Authentication authentication, @PathVariable UUID postId) {
        UUID userId = authenticationService.getCurrentUserId(authentication); //THINK: should posts be visible to everyone?
        Post post = postService.getPostByPostId(postId);
        return ResponseEntity.ok().body(post.asPostDTO());
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PostDTO> createPost(Authentication authentication, @RequestPart("post") PostCommand post, @RequestParam(value = "image", required = true) MultipartFile image) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        Post createdPost = postService.createPost(currentUserId, post, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost.asPostDTO());
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(Authentication authentication, @PathVariable UUID postId, @RequestPart("post") PostCommand postCommand) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        Post post = postService.updatePost(currentUserId, postId, postCommand);
        return ResponseEntity.ok().body(post.asPostDTO());
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDTO> deletePost(Authentication authentication, @PathVariable UUID postId) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        postService.deletePost(currentUserId, postId);
        return ResponseEntity.noContent().build();
    }


    //TODO: move to ReactionController
    @PutMapping("/{postId}/reactions")
    public ResponseEntity<Void> setPostReaction(Authentication authentication, @PathVariable UUID postId) {
        final UUID userId = UUID.fromString((String) authentication.getPrincipal());

        // TODO: Wczytanie typu reakcji z @RequestBody.
        reactionService.setReaction(userId, new ReactionCommand(postId, ReactionType.LIKE));

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/reactions")
    public ResponseEntity<Void> deletePostReaction(Authentication authentication, @PathVariable UUID postId) {
        final UUID userId = UUID.fromString((String) authentication.getPrincipal());

        reactionService.deleteReaction(userId, postId);

        return ResponseEntity.noContent().build();
    }
}

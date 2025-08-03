package pl.aleokaz.backend.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.aleokaz.backend.post.commands.PostCommand;
import pl.aleokaz.backend.security.AuthenticationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PostService postService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts(Authentication authentication, @RequestParam(name = "userId", required = false) UUID authorId) {
        UUID userId = authenticationService.getCurrentUserId(authentication);
        List<Post> posts = postService.getPostsByAuthorId(authorId == null ? userId : authorId);
        if (posts == null || posts.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(postService.postsAsPostDtos(posts), HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(Authentication authentication, @PathVariable UUID postId) {
        UUID userId = authenticationService.getCurrentUserId(authentication); //User is ignored for now, can be used for visibility later
        Post post = postService.getPostByPostId(postId);
        if (post == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(post.asPostDTO(), HttpStatus.OK);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PostDTO> createPost(Authentication authentication, @RequestPart("post") String post, @RequestPart(value = "image", required = true) MultipartFile image) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        PostCommand postCommand;
        Post createdPost;
        try {
            postCommand = objectMapper.readValue(post, PostCommand.class);
            createdPost = postService.createPost(currentUserId, postCommand, image);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (createdPost == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(createdPost.asPostDTO(), HttpStatus.CREATED);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDTO> updatePost(Authentication authentication, @PathVariable UUID postId, @RequestPart("post") PostCommand postCommand) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        Post post = postService.updatePost(currentUserId, postId, postCommand);
        if (post == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(post.asPostDTO(), HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDTO> deletePost(Authentication authentication, @PathVariable UUID postId) {
        UUID currentUserId = authenticationService.getCurrentUserId(authentication);
        postService.deletePost(currentUserId, postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

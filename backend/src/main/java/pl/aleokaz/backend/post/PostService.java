package pl.aleokaz.backend.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import pl.aleokaz.backend.fishingspot.FishingSpotRepository;
import pl.aleokaz.backend.image.ImageSaveException;
import pl.aleokaz.backend.image.ImageService;
import pl.aleokaz.backend.interaction.InteractionMapper;
import pl.aleokaz.backend.post.commands.PostCommand;
import pl.aleokaz.backend.security.AuthorizationException;
import pl.aleokaz.backend.user.UserRepository;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InteractionMapper postMapper;

    @Autowired
    private ImageService imageService;
    @Autowired
    private FishingSpotRepository fishingSpotRepository;

    public PostDTO createPost(@NonNull UUID userId, PostCommand postCommand, MultipartFile image)
            throws ImageSaveException {
        final var author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        final var fishingSpot = fishingSpotRepository.findById(postCommand.fishingSpotId())
            .orElseThrow(() -> new RuntimeException("FishingSpot not found"));

        String imageUrl;
        try {
            imageUrl = imageService.saveImage(image);
        } catch (IOException ioe) {
            throw new ImageSaveException();
        }

        final var post = Post.builder()
                .content(postCommand.content())
                .imageUrl(imageUrl)
                .createdAt(new Date())
                .author(author)
                .fishingSpot(fishingSpot)
                .build();

        final var savedPost = postRepository.save(post);

        return savedPost.asPostDTO();
    }

    public PostDTO updatePost(@NonNull UUID userId, UUID postId, PostCommand postCommand)
            throws AuthorizationException {
        final var author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        final var post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!author.equals(post.author())) {
            throw new AuthorizationException(userId.toString());
        }

        post.content(postCommand.content());
        post.editedAt(new Date());

        final var savedPost = postRepository.save(post);

        return savedPost.asPostDTO();
    }

    public PostDTO deletePost(@NonNull UUID userId, UUID postId) throws AuthorizationException {
        final var author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        final var post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!author.equals(post.author())) {
            throw new AuthorizationException(userId.toString());
        }

        PostDTO responsePost = post.asPostDTO();

        postRepository.delete(post);

        return responsePost;
    }

    public List<PostDTO> getAllPosts(UUID userId) {
        return postRepository.findAll()
                .stream()
                .map(post -> post.asPostDTO())
                .collect(Collectors.toList());
    }

    public List<PostDTO> getPostsByUserId(UUID userId, UUID authorId) {
        return postRepository.findByAuthorId(authorId)
                .stream()
                .map(post -> post.asPostDTO())
                .collect(Collectors.toList());
    }

    public PostDTO getPostById(UUID userId, UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found")) //TODO: Introduce custom exception
                .asPostDTO();
    }
}

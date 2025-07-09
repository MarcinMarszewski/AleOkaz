package pl.aleokaz.backend.post.exceptions;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String postId) {
        super("Post with ID " + postId + " not found");
    }

    public PostNotFoundException() {
        super("Post not found");
    }
}

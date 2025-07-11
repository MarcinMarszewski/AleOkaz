package pl.aleokaz.backend.interaction.exceptions;

public class InteractionNotFoundException extends RuntimeException {
    public InteractionNotFoundException(String message) {
        super(message);
    }

    public InteractionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

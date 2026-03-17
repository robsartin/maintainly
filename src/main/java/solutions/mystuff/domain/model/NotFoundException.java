package solutions.mystuff.domain.model;

/**
 * Thrown when a requested entity cannot be found.
 * Maps to HTTP 404 in the web layer.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}

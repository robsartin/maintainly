package solutions.mystuff.application.web.api;

import java.time.Instant;

import solutions.mystuff.domain.model.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication
        .BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps exceptions from API controllers to JSON error responses.
 *
 * <div class="mermaid">
 * flowchart TD
 *     A[Exception] --> B{Type?}
 *     B -->|NotFoundException| C[404]
 *     B -->|IllegalArgument| D[400]
 *     B -->|BadCredentials| E[401]
 *     B -->|RuntimeException| F[500]
 * </div>
 *
 * @see ApiErrorResponse
 */
@RestControllerAdvice(basePackages =
        "solutions.mystuff.application.web.api")
@Order(1)
public class ApiErrorAdvice {

    private static final Logger log =
            LoggerFactory.getLogger(ApiErrorAdvice.class);

    /** Handles entity-not-found errors — 404. */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFound(
            NotFoundException ex) {
        log.warn("API not found: {}", ex.getMessage());
        return errorResponse(
                HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Handles validation errors — 400. */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequest(
            IllegalArgumentException ex) {
        log.warn("API bad request: {}", ex.getMessage());
        return errorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Handles bad credentials — 401. */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleBadCredentials(
            BadCredentialsException ex) {
        log.warn("API authentication failure");
        return errorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password");
    }

    /** Handles unexpected errors — 500. */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleRuntime(
            RuntimeException ex) {
        log.error("API unexpected error", ex);
        return errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
    }

    private ApiErrorResponse errorResponse(
            HttpStatus status, String message) {
        return new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                Instant.now());
    }
}

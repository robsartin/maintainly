package solutions.mystuff.application.web;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("ControllerErrorAdvice")
class ControllerErrorAdviceTest {

    private final ControllerErrorAdvice advice =
            new ControllerErrorAdvice();

    @Test
    @DisplayName("should not expose raw input in date error")
    void shouldNotExposeRawInput() {
        DateTimeParseException ex =
                new DateTimeParseException(
                        "parse error",
                        "<script>alert(1)</script>", 0);
        Model model = new ConcurrentModel();
        advice.handleDateParseError(ex, model);
        String error = (String) model.getAttribute("error");
        assertFalse(error.contains("<script>"),
                "Error must not contain raw user input");
    }

    @Test
    @DisplayName("should show generic date error message")
    void shouldShowGenericDateMessage() {
        DateTimeParseException ex =
                new DateTimeParseException(
                        "parse error", "bad-date", 0);
        Model model = new ConcurrentModel();
        advice.handleDateParseError(ex, model);
        String error = (String) model.getAttribute("error");
        assertEquals(
                "Invalid date format."
                        + " Please use yyyy-MM-dd.",
                error);
    }

    @Test
    @DisplayName("should handle illegal argument exception")
    void shouldHandleIllegalArgument() {
        IllegalArgumentException ex =
                new IllegalArgumentException("Name required");
        Model model = new ConcurrentModel();
        String view = advice.handleIllegalArgument(
                ex, model);
        assertEquals("items", view);
        assertEquals("Name required",
                model.getAttribute("error"));
    }

    @Test
    @DisplayName("should handle runtime exception with generic message")
    void shouldHandleRuntimeException() {
        RuntimeException ex =
                new RuntimeException("db error");
        Model model = new ConcurrentModel();
        String view = advice.handleRuntimeException(
                ex, model);
        assertEquals("items", view);
        assertEquals("An unexpected error occurred",
                model.getAttribute("error"));
    }
}

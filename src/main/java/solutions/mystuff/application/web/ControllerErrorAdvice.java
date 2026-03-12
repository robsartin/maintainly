package solutions.mystuff.application.web;

import java.time.format.DateTimeParseException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler that renders error messages in the items view.
 *
 * @see ItemController
 * @see ScheduleController
 */
@ControllerAdvice
public class ControllerErrorAdvice {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ControllerErrorAdvice.class);

    /** Handles invalid date format input. */
    @ExceptionHandler(DateTimeParseException.class)
    public String handleDateParseError(
            DateTimeParseException ex, Model model) {
        log.error("Invalid date format: {}",
                ex.getMessage());
        model.addAttribute("error",
                "Invalid date format."
                        + " Please use yyyy-MM-dd.");
        model.addAttribute("items",
                Collections.emptyList());
        return "items";
    }

    /** Handles validation errors from controllers and services. */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(
            IllegalArgumentException ex, Model model) {
        log.error("Invalid argument: {}",
                ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("items",
                Collections.emptyList());
        return "items";
    }

    /** Catches unexpected runtime exceptions as a safety net. */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(
            RuntimeException ex, Model model) {
        log.error("Unexpected error processing request",
                ex);
        model.addAttribute("error",
                "An unexpected error occurred");
        model.addAttribute("items",
                Collections.emptyList());
        return "items";
    }
}

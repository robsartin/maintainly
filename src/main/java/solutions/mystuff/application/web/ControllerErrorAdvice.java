package solutions.mystuff.application.web;

import java.time.format.DateTimeParseException;
import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler that routes errors to the correct view.
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
            DateTimeParseException ex, Model model,
            HttpServletRequest request) {
        log.warn("Invalid date format: {}",
                ex.getMessage());
        model.addAttribute("error",
                "Invalid date format."
                        + " Please use yyyy-MM-dd.");
        return resolveErrorView(model, request);
    }

    /** Handles validation errors from controllers and services. */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(
            IllegalArgumentException ex, Model model,
            HttpServletRequest request) {
        log.warn("Invalid argument: {}",
                ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return resolveErrorView(model, request);
    }

    /** Catches unexpected runtime exceptions as a safety net. */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(
            RuntimeException ex, Model model,
            HttpServletRequest request) {
        log.error("Unexpected error processing request",
                ex);
        model.addAttribute("error",
                "An unexpected error occurred");
        return resolveErrorView(model, request);
    }

    String resolveErrorView(
            Model model, HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/schedules")) {
            model.addAttribute("schedules",
                    Collections.emptyList());
            return "schedules";
        }
        if (path.startsWith("/vendors")) {
            return "redirect:/vendors";
        }
        if (path.startsWith("/settings")) {
            return "redirect:/settings";
        }
        if (path.startsWith("/reports")) {
            return "reports";
        }
        model.addAttribute("items",
                Collections.emptyList());
        return "items";
    }
}

package solutions.mystuff.application.web;

import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Map;

import solutions.mystuff.domain.model.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Global exception handler that routes errors to the correct view
 * with appropriate HTTP status codes.
 *
 * @see ItemController
 * @see ScheduleController
 */
@ControllerAdvice
public class ControllerErrorAdvice {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ControllerErrorAdvice.class);

    private static final Map<String, ErrorViewConfig>
            VIEW_REGISTRY = Map.of(
                    "/schedules",
                    new ErrorViewConfig("schedules",
                            "schedules"),
                    "/vendors",
                    new ErrorViewConfig(
                            "redirect:/vendors", null),
                    "/settings",
                    new ErrorViewConfig(
                            "redirect:/settings", null),
                    "/reports",
                    new ErrorViewConfig("reports", null));

    private static final ErrorViewConfig DEFAULT_VIEW =
            new ErrorViewConfig("items", "items");

    /** Handles invalid date format input — 400 Bad Request. */
    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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

    /** Handles validation errors — 400 Bad Request. */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(
            IllegalArgumentException ex, Model model,
            HttpServletRequest request) {
        log.warn("Invalid argument: {}",
                ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return resolveErrorView(model, request);
    }

    /** Handles entity not found — 404 Not Found. */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(
            NotFoundException ex, Model model,
            HttpServletRequest request) {
        log.warn("Not found: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return resolveErrorView(model, request);
    }

    /** Catches unexpected runtime exceptions — 500 Internal Server Error. */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
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
        for (Map.Entry<String, ErrorViewConfig> entry
                : VIEW_REGISTRY.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return applyConfig(
                        model, entry.getValue());
            }
        }
        return applyConfig(model, DEFAULT_VIEW);
    }

    private String applyConfig(
            Model model, ErrorViewConfig config) {
        if (config.emptyListAttr != null) {
            model.addAttribute(config.emptyListAttr,
                    Collections.emptyList());
        }
        return config.viewName;
    }

    private record ErrorViewConfig(
            String viewName, String emptyListAttr) {
    }
}

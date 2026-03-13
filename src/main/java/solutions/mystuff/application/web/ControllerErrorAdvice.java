package solutions.mystuff.application.web;

import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Map;

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

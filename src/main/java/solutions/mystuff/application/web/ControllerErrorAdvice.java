package solutions.mystuff.application.web;

import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Map;

import solutions.mystuff.domain.model.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler that routes errors to the correct view
 * with appropriate HTTP status codes.
 *
 * @see ItemController
 * @see FacilityController
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
                    "/settings/groups",
                    new ErrorViewConfig(
                            "redirect:/settings/groups",

                            null),
                    "/settings",
                    new ErrorViewConfig(
                            "redirect:/settings", null),
                    "/reports",
                    new ErrorViewConfig("reports", null),
                    "/facilities",
                    new ErrorViewConfig(
                            "redirect:/facilities",
                            null));

    private static final ErrorViewConfig DEFAULT_VIEW =
            new ErrorViewConfig("items", "items");

    /** Handles invalid date format input — 400 Bad Request. */
    @ExceptionHandler(DateTimeParseException.class)
    public String handleDateParseError(
            DateTimeParseException ex, Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.warn("Invalid date format: {}",
                ex.getMessage());
        model.addAttribute("error",
                "Invalid date format."
                        + " Please use yyyy-MM-dd.");
        return resolveErrorView(model, request,
                response, HttpStatus.BAD_REQUEST);
    }

    /** Handles validation errors — 400 Bad Request. */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(
            IllegalArgumentException ex, Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.warn("Invalid argument: {}",
                ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return resolveErrorView(model, request,
                response, HttpStatus.BAD_REQUEST);
    }

    /** Handles entity not found — 404 Not Found. */
    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(
            NotFoundException ex, Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.warn("Not found: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return resolveErrorView(model, request,
                response, HttpStatus.NOT_FOUND);
    }

    /** Catches unexpected runtime exceptions — 500 Internal Server Error. */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(
            RuntimeException ex, Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        log.error("Unexpected error processing request",
                ex);
        model.addAttribute("error",
                "An unexpected error occurred");
        return resolveErrorView(model, request,
                response,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    String resolveErrorView(
            Model model, HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status) {
        String path = request.getRequestURI();
        String bestKey = null;
        for (String key : VIEW_REGISTRY.keySet()) {
            if (path.startsWith(key)
                    && (bestKey == null
                    || key.length()
                            > bestKey.length())) {
                bestKey = key;
            }
        }
        if (bestKey != null) {
            return applyConfig(model, response,
                    status,
                    VIEW_REGISTRY.get(bestKey));
        }
        return applyConfig(model, response,
                status, DEFAULT_VIEW);
    }

    private String applyConfig(
            Model model, HttpServletResponse response,
            HttpStatus status, ErrorViewConfig config) {
        if (config.emptyListAttr != null) {
            model.addAttribute(config.emptyListAttr,
                    Collections.emptyList());
        }
        if (!config.viewName.startsWith("redirect:")) {
            response.setStatus(status.value());
        }
        return config.viewName;
    }

    private record ErrorViewConfig(
            String viewName, String emptyListAttr) {
    }
}

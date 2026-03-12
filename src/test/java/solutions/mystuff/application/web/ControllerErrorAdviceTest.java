package solutions.mystuff.application.web;

import java.time.format.DateTimeParseException;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        advice.handleDateParseError(
                ex, model, itemRequest());
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
        advice.handleDateParseError(
                ex, model, itemRequest());
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
                new IllegalArgumentException(
                        "Name required");
        Model model = new ConcurrentModel();
        String view = advice.handleIllegalArgument(
                ex, model, itemRequest());
        assertEquals("items", view);
        assertEquals("Name required",
                model.getAttribute("error"));
    }

    @Test
    @DisplayName("should handle runtime exception")
    void shouldHandleRuntimeException() {
        RuntimeException ex =
                new RuntimeException("db error");
        Model model = new ConcurrentModel();
        String view = advice.handleRuntimeException(
                ex, model, itemRequest());
        assertEquals("items", view);
        assertEquals("An unexpected error occurred",
                model.getAttribute("error"));
    }

    @Test
    @DisplayName("should route schedule errors to schedules view")
    void shouldRouteScheduleErrors() {
        IllegalArgumentException ex =
                new IllegalArgumentException("bad input");
        Model model = new ConcurrentModel();
        String view = advice.handleIllegalArgument(
                ex, model, requestForPath("/schedules/log"));
        assertEquals("schedules", view);
    }

    @Test
    @DisplayName("should route settings errors to redirect")
    void shouldRouteSettingsErrors() {
        IllegalArgumentException ex =
                new IllegalArgumentException("bad input");
        Model model = new ConcurrentModel();
        String view = advice.handleIllegalArgument(
                ex, model, requestForPath("/settings/org-image"));
        assertEquals("redirect:/settings", view);
    }

    @Test
    @DisplayName("should route vendor errors to vendors redirect")
    void shouldRouteVendorErrors() {
        IllegalArgumentException ex =
                new IllegalArgumentException("bad input");
        Model model = new ConcurrentModel();
        String view = advice.handleIllegalArgument(
                ex, model,
                requestForPath("/vendors/import"));
        assertEquals("redirect:/vendors", view);
    }

    @Test
    @DisplayName("should route report errors to reports view")
    void shouldRouteReportErrors() {
        IllegalArgumentException ex =
                new IllegalArgumentException("bad input");
        Model model = new ConcurrentModel();
        String view = advice.handleIllegalArgument(
                ex, model, requestForPath("/reports/item-history"));
        assertEquals("reports", view);
    }

    private HttpServletRequest itemRequest() {
        return requestForPath("/items/add");
    }

    private HttpServletRequest requestForPath(
            String path) {
        HttpServletRequest req =
                mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(path);
        return req;
    }
}

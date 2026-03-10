package com.robsartin.maintainly.application.web;

import java.time.format.DateTimeParseException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerErrorAdvice {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ControllerErrorAdvice.class);

    @ExceptionHandler(DateTimeParseException.class)
    public String handleDateParseError(
            DateTimeParseException ex, Model model) {
        log.error("Invalid date format: {}",
                ex.getMessage());
        model.addAttribute("error",
                "Invalid date format: "
                        + ex.getParsedString());
        model.addAttribute("items",
                Collections.emptyList());
        return "items";
    }

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

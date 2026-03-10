package com.robsartin.maintainly.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.model.Property;
import com.robsartin.maintainly.domain.model.ServiceRequest;
import com.robsartin.maintainly.domain.model.UuidV7;
import com.robsartin.maintainly.domain.port.in.UserResolver;
import com.robsartin.maintainly.domain.port.out.PropertyRepository;
import com.robsartin.maintainly.domain.port.out.ServiceRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PropertyController {

    private static final Logger log =
            LoggerFactory.getLogger(PropertyController.class);
    private static final String MDC_ORG_ID = "organizationId";

    private final PropertyRepository propertyRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserResolver userResolver;

    public PropertyController(
            PropertyRepository propertyRepository,
            ServiceRequestRepository serviceRequestRepository,
            UserResolver userResolver) {
        this.propertyRepository = propertyRepository;
        this.serviceRequestRepository =
                serviceRequestRepository;
        this.userResolver = userResolver;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String q,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        if (!user.hasOrganization()) {
            log.warn("User {} has no organization",
                    user.getUsername());
            model.addAttribute("noOrganization", true);
            model.addAttribute("properties",
                    Collections.emptyList());
            return "home";
        }
        UUID orgId = user.getOrganization().getId();
        MDC.put(MDC_ORG_ID, orgId.toString());
        try {
            List<Property> properties;
            if (q != null && !q.isBlank()) {
                log.info("Searching properties for query={}",
                        q);
                properties = propertyRepository
                        .searchByOrganizationId(orgId, q);
                model.addAttribute("q", q);
            } else {
                log.info("Listing all properties");
                properties = propertyRepository
                        .findByOrganizationIdOrderByNextServiceDate(
                                orgId);
            }
            model.addAttribute("properties", properties);
            model.addAttribute("username",
                    user.getUsername());
            return "home";
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @PostMapping("/properties/service")
    public String addService(
            @RequestParam UUID propertyId,
            @RequestParam String description,
            @RequestParam String serviceDate,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        setOrgMdc(user);
        try {
            LocalDate date = LocalDate.parse(serviceDate);
            ServiceRequest sr = new ServiceRequest();
            sr.setId(UuidV7.generate());
            sr.setPropertyId(propertyId);
            sr.setDescription(description);
            sr.setServiceDate(date);
            serviceRequestRepository.save(sr);
            log.info("Created service request {} "
                    + "for property {}",
                    sr.getId(), propertyId);
            return index(null, principal, model);
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @PostMapping("/properties/service/complete")
    public String completeService(
            @RequestParam UUID serviceRequestId,
            Principal principal, Model model) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        setOrgMdc(user);
        try {
            serviceRequestRepository.markCompleted(
                    serviceRequestId);
            log.info("Completed service request {}",
                    serviceRequestId);
            return index(null, principal, model);
        } finally {
            MDC.remove(MDC_ORG_ID);
        }
    }

    @ExceptionHandler(DateTimeParseException.class)
    public String handleDateParseError(
            DateTimeParseException ex, Model model) {
        log.error("Invalid date format: {}", ex.getMessage());
        model.addAttribute("error",
                "Invalid date format: " + ex.getParsedString());
        model.addAttribute("properties",
                Collections.emptyList());
        return "home";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(
            IllegalArgumentException ex, Model model) {
        log.error("Invalid argument: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("properties",
                Collections.emptyList());
        return "home";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(
            RuntimeException ex, Model model) {
        log.error("Unexpected error processing request",
                ex);
        model.addAttribute("error",
                "An unexpected error occurred");
        model.addAttribute("properties",
                Collections.emptyList());
        return "home";
    }

    private void setOrgMdc(AppUser user) {
        if (user.hasOrganization()) {
            MDC.put(MDC_ORG_ID,
                    user.getOrganization().getId().toString());
        }
    }
}

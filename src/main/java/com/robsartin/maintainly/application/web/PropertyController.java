package com.robsartin.maintainly.application.web;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.model.Property;
import com.robsartin.maintainly.domain.model.ServiceRequest;
import com.robsartin.maintainly.domain.port.out.PropertyRepository;
import com.robsartin.maintainly.domain.port.out.ServiceRequestRepository;
import com.robsartin.maintainly.domain.port.in.UserResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PropertyController {

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
            model.addAttribute("noOrganization", true);
            model.addAttribute("properties",
                    Collections.emptyList());
            return "home";
        }
        int orgId = user.getOrganization().getId();
        List<Property> properties;
        if (q != null && !q.isBlank()) {
            properties = propertyRepository
                    .searchByOrganizationId(orgId, q);
            model.addAttribute("q", q);
        } else {
            properties = propertyRepository
                    .findByOrganizationIdOrderByNextServiceDate(
                            orgId);
        }
        model.addAttribute("properties", properties);
        model.addAttribute("username", user.getUsername());
        return "home";
    }

    @PostMapping("/properties/service")
    public String addService(
            @RequestParam UUID propertyId,
            @RequestParam String description,
            @RequestParam String serviceDate,
            Principal principal, Model model) {
        ServiceRequest sr = new ServiceRequest();
        sr.setId(UUID.randomUUID());
        sr.setPropertyId(propertyId);
        sr.setDescription(description);
        sr.setServiceDate(LocalDate.parse(serviceDate));
        serviceRequestRepository.save(sr);
        return index(null, principal, model);
    }

    @PostMapping("/properties/service/complete")
    public String completeService(
            @RequestParam UUID serviceRequestId,
            Principal principal, Model model) {
        serviceRequestRepository.markCompleted(
                serviceRequestId);
        return index(null, principal, model);
    }
}

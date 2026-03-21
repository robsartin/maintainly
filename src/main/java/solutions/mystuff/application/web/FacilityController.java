package solutions.mystuff.application.web;

import java.security.Principal;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.FacilityData;
import solutions.mystuff.domain.port.in.FacilityManagement;
import solutions.mystuff.domain.port.in.FacilityQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation
        .DeleteMapping;
import org.springframework.web.bind.annotation
        .GetMapping;
import org.springframework.web.bind.annotation
        .PathVariable;
import org.springframework.web.bind.annotation
        .PostMapping;
import org.springframework.web.bind.annotation
        .PutMapping;
import org.springframework.web.bind.annotation
        .RequestParam;
import org.springframework.web.servlet.mvc
        .support.RedirectAttributes;

/**
 * Handles facility CRUD at /facilities endpoints.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser-&gt;&gt;FacilityController: GET/POST/PUT/DELETE
 *     FacilityController-&gt;&gt;FacilityManagement: create/update/delete
 *     FacilityController-&gt;&gt;FacilityQuery: findAll
 *     FacilityController--&gt;&gt;Browser: HTML (Thymeleaf)
 * </div>
 *
 * @see ControllerHelper
 * @see FacilityManagement
 * @see FacilityQuery
 */
@Controller
@Tag(name = "Facilities",
        description = "Facility CRUD operations")
public class FacilityController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    FacilityController.class);

    private final ControllerHelper helper;
    private final FacilityManagement facilityService;
    private final FacilityQuery facilityQuery;

    public FacilityController(
            ControllerHelper helper,
            FacilityManagement facilityService,
            FacilityQuery facilityQuery) {
        this.helper = helper;
        this.facilityService = facilityService;
        this.facilityQuery = facilityQuery;
    }

    @Operation(summary = "List facilities",
            description = "Returns all facilities for"
                    + " the organization.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "HTML page with"
                            + " facility table"))
    @GetMapping("/facilities")
    public String facilities(
            Principal principal, Model model) {
        AppUser user =
                helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return helper.handleNoOrg(
                    user, model, "facilities");
        }
        helper.setOrgMdc(user);
        helper.addUserAttrs(user, model);
        model.addAttribute("facilities",
                facilityQuery.findAllFacilities(
                        user.getOrganization()
                                .getId()));
        return "facilities";
    }

    @Operation(summary = "Create facility",
            description = "Creates a new facility.",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirect to"
                                    + " /facilities"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation"
                                    + " error")})
    @PostMapping("/facilities")
    public String addFacility(
            @Parameter(description = "Facility name"
                    + " (required, max 200 chars)",
                    required = true)
            @RequestParam String name,
            @Parameter(description = "Street address"
                    + " line 1 (max 200 chars)")
            @RequestParam(required = false)
                    String addressLine1,
            @Parameter(description = "Street address"
                    + " line 2 (max 200 chars)")
            @RequestParam(required = false)
                    String addressLine2,
            @Parameter(description = "City"
                    + " (max 100 chars)")
            @RequestParam(required = false)
                    String city,
            @Parameter(description = "State or"
                    + " province (max 100 chars)")
            @RequestParam(required = false)
                    String stateProvince,
            @Parameter(description = "Postal/ZIP"
                    + " code (max 30 chars)")
            @RequestParam(required = false)
                    String postalCode,
            @Parameter(description = "Country"
                    + " (max 100 chars)")
            @RequestParam(required = false)
                    String country,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user =
                helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId =
                user.getOrganization().getId();
        FacilityData data = new FacilityData(
                name, addressLine1, addressLine2,
                city, stateProvince, postalCode,
                country);
        facilityService.createFacility(orgId, data);
        redirectAttrs.addFlashAttribute(
                "success", "Facility created");
        return "redirect:/facilities";
    }

    @Operation(summary = "Update facility",
            description = "Replaces all fields on"
                    + " an existing facility.",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirect to"
                                    + " /facilities"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation"
                                    + " error"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Facility"
                                    + " not found")})
    @PutMapping("/facilities/{id}")
    public String editFacility(
            @Parameter(description = "Facility UUID")
            @PathVariable("id") UUID facilityId,
            @Parameter(description = "Facility name"
                    + " (required, max 200 chars)",
                    required = true)
            @RequestParam String name,
            @Parameter(description = "Street address"
                    + " line 1 (max 200 chars)")
            @RequestParam(required = false)
                    String addressLine1,
            @Parameter(description = "Street address"
                    + " line 2 (max 200 chars)")
            @RequestParam(required = false)
                    String addressLine2,
            @Parameter(description = "City"
                    + " (max 100 chars)")
            @RequestParam(required = false)
                    String city,
            @Parameter(description = "State or"
                    + " province (max 100 chars)")
            @RequestParam(required = false)
                    String stateProvince,
            @Parameter(description = "Postal/ZIP"
                    + " code (max 30 chars)")
            @RequestParam(required = false)
                    String postalCode,
            @Parameter(description = "Country"
                    + " (max 100 chars)")
            @RequestParam(required = false)
                    String country,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user =
                helper.resolveUser(principal);
        helper.setOrgMdc(user);
        UUID orgId =
                user.getOrganization().getId();
        FacilityData data = new FacilityData(
                name, addressLine1, addressLine2,
                city, stateProvince, postalCode,
                country);
        facilityService.updateFacility(
                orgId, facilityId, data);
        redirectAttrs.addFlashAttribute(
                "success", "Facility updated");
        return "redirect:/facilities";
    }

    @Operation(summary = "Delete facility",
            description = "Permanently deletes a"
                    + " facility. Items referencing"
                    + " this facility will have their"
                    + " facility_id set to null.",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirect to"
                                    + " /facilities"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Facility"
                                    + " not found")})
    @DeleteMapping("/facilities/{id}")
    public String deleteFacility(
            @Parameter(description = "Facility UUID")
            @PathVariable("id") UUID facilityId,
            Principal principal,
            RedirectAttributes redirectAttrs) {
        AppUser user =
                helper.resolveUser(principal);
        helper.setOrgMdc(user);
        facilityService.deleteFacility(
                user.getOrganization().getId(),
                facilityId);
        redirectAttrs.addFlashAttribute(
                "success", "Facility deleted");
        return "redirect:/facilities";
    }
}

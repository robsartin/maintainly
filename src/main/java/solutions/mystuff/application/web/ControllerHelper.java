package solutions.mystuff.application.web;

import java.security.Principal;
import java.util.Collections;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.LogSanitizer;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.UserResolver;
import solutions.mystuff.domain.port.in.VendorManagement;
import solutions.mystuff.domain.port.in.VendorQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.oauth2.client
        .authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user
        .OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

/**
 * Shared helper used by all controllers for user resolution and MDC.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ControllerHelper {
 *         +resolveUser(Principal) AppUser
 *         +addUserAttrs(AppUser, Model) void
 *         +setOrgMdc(AppUser) void
 *
 *         +clampSize(int) int
 *         +resolveVendor(...) Vendor
 *         +handleNoOrg(AppUser, Model, String) String
 *     }
 * </div>
 *
 * @see solutions.mystuff.domain.port.in.UserResolver
 */
@Component
public class ControllerHelper {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ControllerHelper.class);
    private static final String MDC_ORG_ID =
            "organizationId";
    private static final int MAX_PAGE_SIZE = 100;
    private static final String NEW_VENDOR_SENTINEL =
            "__new__";

    private final UserResolver userResolver;
    private final VendorManagement vendorService;
    private final VendorQuery vendorQuery;

    public ControllerHelper(
            UserResolver userResolver,
            VendorManagement vendorService,
            VendorQuery vendorQuery) {
        this.userResolver = userResolver;
        this.vendorService = vendorService;
        this.vendorQuery = vendorQuery;
    }

    /** Resolves the authenticated principal to an AppUser. */
    public AppUser resolveUser(Principal principal) {
        return userResolver.resolveOrCreate(
                extractUsername(principal));
    }

    private String extractUsername(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken
                oauth) {
            OAuth2User user = oauth.getPrincipal();
            String email = user.getAttribute("email");
            if (email != null && !email.isBlank()) {
                return LogSanitizer.sanitize(email);
            }
        }
        return LogSanitizer.sanitize(
                principal.getName());
    }

    /** Adds username and organization attributes to the model. */
    void addUserAttrs(AppUser user, Model model) {
        model.addAttribute("username",
                user.getUsername());
        model.addAttribute("organization",
                user.getOrganization());
        model.addAttribute("currentUser", user);
    }

    /** Sets the organization ID in the MDC for log correlation. */
    public void setOrgMdc(AppUser user) {
        if (user.hasOrganization()) {
            MDC.put(MDC_ORG_ID,
                    user.getOrganization().getId()
                            .toString());
        }
    }

    /** Clamps page size between 1 and the configured maximum. */
    int clampSize(int size) {
        return Math.max(1, Math.min(size, MAX_PAGE_SIZE));
    }

    /** Resolves a vendor from form parameters: existing, new, or none. */
    Vendor resolveVendor(
            UUID orgId, String vendorId,
            String newVendorName,
            String newVendorPhone) {
        if (NEW_VENDOR_SENTINEL.equals(vendorId)) {
            return vendorService.createVendor(
                    orgId, newVendorName,
                    newVendorPhone);
        }
        if (vendorId != null && !vendorId.isBlank()) {
            UUID id = UUID.fromString(vendorId);
            return vendorQuery.findAllVendors(orgId)
                    .stream()
                    .filter(v -> v.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "Vendor not found"));
        }
        return null;
    }

    /** Handles missing organization by returning a view with an empty list. */
    String handleNoOrg(
            AppUser user, Model model,
            String viewName) {
        log.warn("User {} has no organization",
                user.getUsername());
        model.addAttribute("noOrganization", true);
        model.addAttribute(viewName,
                Collections.emptyList());
        return viewName;
    }
}

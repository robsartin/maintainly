package solutions.mystuff.application.web.api;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.model.VendorData;
import solutions.mystuff.domain.port.in.UserResolver;
import solutions.mystuff.domain.port.in.VendorManagement;
import solutions.mystuff.domain.port.in.VendorQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for vendor CRUD operations.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Client->>VendorApiController: GET /api/v1/vendors
 *     VendorApiController->>UserResolver: resolveOrCreate
 *     VendorApiController->>VendorQuery: findAllVendors
 *     VendorApiController-->>Client: JSON List
 * </div>
 *
 * @see VendorQuery
 * @see VendorManagement
 */
@RestController
@RequestMapping("/api/v1/vendors")
@Tag(name = "Vendors API",
        description = "REST API for vendor management")
public class VendorApiController {

    private final VendorQuery vendorQuery;
    private final VendorManagement vendorService;
    private final UserResolver userResolver;

    /** Creates a vendor API controller. */
    public VendorApiController(
            VendorQuery vendorQuery,
            VendorManagement vendorService,
            UserResolver userResolver) {
        this.vendorQuery = vendorQuery;
        this.vendorService = vendorService;
        this.userResolver = userResolver;
    }

    /** Lists all vendors for the organization. */
    @Operation(summary = "List vendors")
    @GetMapping
    public List<VendorResponse> list(
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        return vendorQuery.findAllVendors(orgId)
                .stream()
                .map(VendorResponse::from)
                .toList();
    }

    /** Gets a single vendor by ID. */
    @Operation(summary = "Get vendor by ID")
    @GetMapping("/{id}")
    public VendorResponse get(
            @PathVariable UUID id,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        Vendor v = vendorQuery.findVendor(id, orgId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Vendor not found"));
        return VendorResponse.from(v);
    }

    /** Creates a new vendor. */
    @Operation(summary = "Create vendor")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VendorResponse create(
            @RequestBody VendorData data,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        return VendorResponse.from(
                vendorService.createVendor(
                        orgId, data));
    }

    /** Updates an existing vendor. */
    @Operation(summary = "Update vendor")
    @PutMapping("/{id}")
    public VendorResponse update(
            @PathVariable UUID id,
            @RequestBody VendorData data,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        return VendorResponse.from(
                vendorService.updateVendor(
                        orgId, id, data));
    }

    /** Deletes a vendor. */
    @Operation(summary = "Delete vendor")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id,
            Principal principal) {
        UUID orgId = resolveOrgId(principal);
        vendorService.deleteVendor(orgId, id);
    }

    private UUID resolveOrgId(Principal principal) {
        AppUser user = userResolver.resolveOrCreate(
                principal.getName());
        if (!user.hasOrganization()) {
            throw new IllegalArgumentException(
                    "No organization assigned");
        }
        return user.getOrganization().getId();
    }
}

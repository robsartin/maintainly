package solutions.mystuff.application.web;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.ParsedAltPhone;
import solutions.mystuff.domain.model.VendorData;
import solutions.mystuff.domain.port.in.VendorImportExport;
import solutions.mystuff.domain.port.in.VendorManagement;
import solutions.mystuff.domain.port.in.VendorQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles vendor CRUD, import, and export at /vendors endpoints.
 *
 * @see ControllerHelper
 * @see VendorManagement
 * @see VendorImportExport
 */
@Controller
@Tag(name = "Vendors",
        description = "Vendor CRUD, vCard import/export")
public class VendorController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    VendorController.class);

    private final ControllerHelper helper;
    private final VendorManagement vendorService;
    private final VendorQuery vendorQuery;
    private final VendorImportExport importExport;

    public VendorController(
            ControllerHelper helper,
            VendorManagement vendorService,
            VendorQuery vendorQuery,
            VendorImportExport importExport) {
        this.helper = helper;
        this.vendorService = vendorService;
        this.vendorQuery = vendorQuery;
        this.importExport = importExport;
    }

    @Operation(summary = "List vendors",
            description = "Returns all vendors for"
                    + " the organization. Model"
                    + " attributes: vendors"
                    + " (List<Vendor>), each with"
                    + " name, phone, email, address"
                    + " fields, website, notes, and"
                    + " altPhones"
                    + " (List<VendorAltPhone>).",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "HTML page with"
                            + " vendor table"))
    @GetMapping("/vendors")
    public String vendors(
            Principal principal, Model model) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasOrganization()) {
            return helper.handleNoOrg(user, model,
                    "vendors");
        }
        helper.setOrgMdc(user);
        try {
            helper.addUserAttrs(user, model);
            model.addAttribute("vendors",
                    vendorQuery.findAllVendors(
                            user.getOrganization()
                                    .getId()));
            return "vendors";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @Operation(summary = "Create vendor",
            description = "Creates a new vendor with"
                    + " contact details and optional"
                    + " alternate phone numbers."
                    + " Alt phones are submitted as"
                    + " parallel arrays.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /vendors on"
                                    + " success"),
                    @ApiResponse(responseCode = "200",
                            description = "Validation"
                                    + " error (blank"
                                    + " name)")})
    @PostMapping("/vendors")
    public String addVendor(
            @Parameter(description = "Vendor name"
                    + " (required, max 200 chars)",
                    required = true)
            @RequestParam String name,
            @Parameter(description = "Primary phone"
                    + " (max 50 chars)")
            @RequestParam(required = false) String phone,
            @Parameter(description = "Email address"
                    + " (max 320 chars)")
            @RequestParam(required = false) String email,
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
            @RequestParam(required = false) String city,
            @Parameter(description = "State or"
                    + " province (max 100 chars)")
            @RequestParam(required = false)
                    String stateProvince,
            @Parameter(description = "Postal/ZIP code"
                    + " (max 30 chars)")
            @RequestParam(required = false)
                    String postalCode,
            @Parameter(description = "Country"
                    + " (max 100 chars)")
            @RequestParam(required = false)
                    String country,
            @Parameter(description = "Website URL"
                    + " (max 2000 chars)")
            @RequestParam(required = false)
                    String website,
            @Parameter(description = "Free-text notes"
                    + " (max 2000 chars)")
            @RequestParam(required = false) String notes,
            @Parameter(description = "Alt phone numbers"
                    + " (parallel array with"
                    + " altPhoneLabel, max 50 chars"
                    + " each)")
            @RequestParam(required = false)
                    List<String> altPhoneNumber,
            @Parameter(description = "Alt phone labels"
                    + " (e.g. 'mobile', 'after-hours',"
                    + " max 50 chars each)")
            @RequestParam(required = false)
                    List<String> altPhoneLabel,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId =
                    user.getOrganization().getId();
            VendorData data = new VendorData(
                    name, phone, email, addressLine1,
                    addressLine2, city, stateProvince,
                    postalCode, country, website, notes,
                    buildAltPhones(altPhoneNumber,
                            altPhoneLabel));
            vendorService.createVendor(orgId, data);
            return "redirect:/vendors";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @Operation(summary = "Update vendor",
            description = "Replaces all fields on an"
                    + " existing vendor including alt"
                    + " phones. Omitted alt phones are"
                    + " removed.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /vendors on"
                                    + " success"),
                    @ApiResponse(responseCode = "200",
                            description = "Validation"
                                    + " error (blank"
                                    + " name, vendor"
                                    + " not found)")})
    @PutMapping("/vendors/{id}")
    public String editVendor(
            @Parameter(description = "Vendor UUID")
            @PathVariable("id") UUID vendorId,
            @Parameter(description = "Vendor name"
                    + " (required, max 200 chars)",
                    required = true)
            @RequestParam String name,
            @Parameter(description = "Primary phone"
                    + " (max 50 chars)")
            @RequestParam(required = false) String phone,
            @Parameter(description = "Email address"
                    + " (max 320 chars)")
            @RequestParam(required = false) String email,
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
            @RequestParam(required = false) String city,
            @Parameter(description = "State or"
                    + " province (max 100 chars)")
            @RequestParam(required = false)
                    String stateProvince,
            @Parameter(description = "Postal/ZIP code"
                    + " (max 30 chars)")
            @RequestParam(required = false)
                    String postalCode,
            @Parameter(description = "Country"
                    + " (max 100 chars)")
            @RequestParam(required = false)
                    String country,
            @Parameter(description = "Website URL"
                    + " (max 2000 chars)")
            @RequestParam(required = false)
                    String website,
            @Parameter(description = "Free-text notes"
                    + " (max 2000 chars)")
            @RequestParam(required = false) String notes,
            @Parameter(description = "Alt phone numbers"
                    + " (parallel array with"
                    + " altPhoneLabel)")
            @RequestParam(required = false)
                    List<String> altPhoneNumber,
            @Parameter(description = "Alt phone labels")
            @RequestParam(required = false)
                    List<String> altPhoneLabel,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            UUID orgId =
                    user.getOrganization().getId();
            VendorData data = new VendorData(
                    name, phone, email, addressLine1,
                    addressLine2, city, stateProvince,
                    postalCode, country, website, notes,
                    buildAltPhones(altPhoneNumber,
                            altPhoneLabel));
            vendorService.updateVendor(
                    orgId, vendorId, data);
            return "redirect:/vendors";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @Operation(summary = "Delete vendor",
            description = "Permanently deletes a"
                    + " vendor. Schedules referencing"
                    + " this vendor will have their"
                    + " preferred_vendor set to null.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /vendors"),
                    @ApiResponse(responseCode = "200",
                            description = "Error if"
                                    + " vendor not"
                                    + " found")})
    @DeleteMapping("/vendors/{id}")
    public String deleteVendor(
            @Parameter(description = "Vendor UUID")
            @PathVariable("id") UUID vendorId,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            vendorService.deleteVendor(
                    user.getOrganization().getId(),
                    vendorId);
            return "redirect:/vendors";
        } finally {
            helper.clearOrgMdc();
        }
    }

    @Operation(summary = "Export all vendors",
            description = "Downloads all vendors as"
                    + " a vCard 4.0 (.vcf) file."
                    + " Includes name, phone, email,"
                    + " address, and alt phones as"
                    + " additional TEL properties.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "vCard file download",
                    content = @Content(
                            mediaType = "text/vcard")))
    @GetMapping("/vendors/export")
    public ResponseEntity<byte[]> exportAll(
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            String vcf = importExport
                    .exportAllVendors(
                            user.getOrganization()
                                    .getId());
            return vcfResponse(vcf, "vendors.vcf");
        } finally {
            helper.clearOrgMdc();
        }
    }

    @Operation(summary = "Export single vendor",
            description = "Downloads one vendor as"
                    + " a vCard 4.0 (.vcf) file.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "vCard file download",
                    content = @Content(
                            mediaType = "text/vcard")))
    @GetMapping("/vendors/export/{id}")
    public ResponseEntity<byte[]> exportOne(
            @Parameter(description = "Vendor UUID")
            @PathVariable("id") UUID id,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            String vcf = importExport.exportVendor(
                    user.getOrganization().getId(),
                    id);
            return vcfResponse(vcf, "vendor.vcf");
        } finally {
            helper.clearOrgMdc();
        }
    }

    @Operation(summary = "Import vendors",
            description = "Imports vendors from an"
                    + " uploaded vCard (.vcf) file."
                    + " Supports vCard 3.0 and 4.0."
                    + " Multiple TEL properties become"
                    + " alt phones. Duplicate names"
                    + " create new vendors.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /vendors on"
                                    + " success"),
                    @ApiResponse(responseCode = "200",
                            description = "Error if"
                                    + " file empty or"
                                    + " unreadable")})
    @PostMapping("/vendors/import")
    public String importVendors(
            @Parameter(description = "vCard (.vcf)"
                    + " file to import")
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        helper.setOrgMdc(user);
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException(
                        "No file selected");
            }
            String content = new String(
                    file.getBytes(),
                    StandardCharsets.UTF_8);
            int count = importExport.importVendors(
                    user.getOrganization().getId(),
                    content).size();
            log.info("Imported {} vendors", count);
            return "redirect:/vendors";
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to read file", e);
        } finally {
            helper.clearOrgMdc();
        }
    }

    private List<ParsedAltPhone> buildAltPhones(
            List<String> phones, List<String> labels) {
        if (phones == null || phones.isEmpty()) {
            return List.of();
        }
        List<ParsedAltPhone> result = new ArrayList<>();
        for (int i = 0; i < phones.size(); i++) {
            String phone = phones.get(i);
            String label = labels != null
                    && i < labels.size()
                    ? labels.get(i) : null;
            if (phone != null && !phone.isBlank()) {
                result.add(new ParsedAltPhone(
                        phone, label));
            }
        }
        return result;
    }

    private ResponseEntity<byte[]> vcfResponse(
            String vcf, String filename) {
        byte[] bytes = vcf.getBytes(
                StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE,
                "text/vcard; charset=utf-8");
        headers.set(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                        + filename + "\"");
        return new ResponseEntity<>(
                bytes, headers, HttpStatus.OK);
    }
}

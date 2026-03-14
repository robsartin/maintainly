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

    /** Lists all vendors for the organization. */
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

    /** Creates a new vendor. */
    @PostMapping("/vendors")
    public String addVendor(
            @RequestParam String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false)
                    String addressLine1,
            @RequestParam(required = false)
                    String addressLine2,
            @RequestParam(required = false) String city,
            @RequestParam(required = false)
                    String stateProvince,
            @RequestParam(required = false)
                    String postalCode,
            @RequestParam(required = false)
                    String country,
            @RequestParam(required = false)
                    String website,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false)
                    List<String> altPhoneNumber,
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

    /** Updates an existing vendor. */
    @PutMapping("/vendors/{id}")
    public String editVendor(
            @PathVariable("id") UUID vendorId,
            @RequestParam String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false)
                    String addressLine1,
            @RequestParam(required = false)
                    String addressLine2,
            @RequestParam(required = false) String city,
            @RequestParam(required = false)
                    String stateProvince,
            @RequestParam(required = false)
                    String postalCode,
            @RequestParam(required = false)
                    String country,
            @RequestParam(required = false)
                    String website,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false)
                    List<String> altPhoneNumber,
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

    /** Deletes a vendor. */
    @DeleteMapping("/vendors/{id}")
    public String deleteVendor(
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

    /** Exports all vendors as a vCard file. */
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

    /** Exports a single vendor as a vCard file. */
    @GetMapping("/vendors/export/{id}")
    public ResponseEntity<byte[]> exportOne(
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

    /** Imports vendors from an uploaded vCard file. */
    @PostMapping("/vendors/import")
    public String importVendors(
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

package solutions.mystuff.application.web;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.port.in.VendorImportExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles vendor vCard import and export at /vendors endpoints.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser->>VendorController: GET /vendors/export
 *     VendorController->>ControllerHelper: resolveUser(principal)
 *     VendorController->>VendorImportExport: exportAllVendors()
 *     VendorController-->>Browser: vendors.vcf attachment
 * </div>
 *
 * @see ControllerHelper
 * @see VendorImportExport
 */
@Controller
public class VendorController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    VendorController.class);

    private final ControllerHelper helper;
    private final VendorImportExport importExport;

    public VendorController(
            ControllerHelper helper,
            VendorImportExport importExport) {
        this.helper = helper;
        this.importExport = importExport;
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
            @PathVariable("id") java.util.UUID id,
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
            return "redirect:/settings";
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to read file", e);
        } finally {
            helper.clearOrgMdc();
        }
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

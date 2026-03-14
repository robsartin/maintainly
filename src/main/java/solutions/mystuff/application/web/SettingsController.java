package solutions.mystuff.application.web;

import java.security.Principal;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Organization;
import solutions.mystuff.domain.port.in.ProfileImageUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Manages user and organization settings including profile images.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Browser->>SettingsController: GET/POST /settings/**
 *     SettingsController->>ControllerHelper: resolveUser(principal)
 *     SettingsController->>ProfileImageUpload: saveOrgImage/saveUserImage
 *     ProfileImageUpload->>Repository: persist image bytes
 *     Repository-->>SettingsController: result
 *     SettingsController-->>Browser: Thymeleaf view or image bytes
 * </div>
 *
 * @see ControllerHelper
 * @see solutions.mystuff.domain.port.in.ProfileImageUpload
 */
@Controller
@Tag(name = "Settings",
        description = "User and organization settings"
                + " including profile images")
public class SettingsController {

    private final ControllerHelper helper;
    private final ProfileImageUpload imageService;

    public SettingsController(
            ControllerHelper helper,
            ProfileImageUpload imageService) {
        this.helper = helper;
        this.imageService = imageService;
    }

    @Operation(summary = "Settings page",
            description = "Renders the settings page"
                    + " showing the current user's"
                    + " profile and organization"
                    + " details. Model attributes:"
                    + " user (AppUser) with username,"
                    + " email, profileImage flag,"
                    + " organization name and image.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "HTML settings page"))
    @GetMapping("/settings")
    public String settings(
            Principal principal, Model model) {
        AppUser user = helper.resolveUser(principal);
        helper.addUserAttrs(user, model);
        model.addAttribute("user", user);
        return "settings";
    }

    @Operation(summary = "Upload organization image",
            description = "Uploads and replaces the"
                    + " organization's profile image."
                    + " Image is resized to 128x128px."
                    + " Max file size: 512KB.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /settings on"
                                    + " success"),
                    @ApiResponse(responseCode = "200",
                            description = "Error if no"
                                    + " file or no"
                                    + " organization")})
    @PutMapping("/settings/org-image")
    public String uploadOrgImage(
            @Parameter(description = "Image file"
                    + " (PNG, JPG, GIF; max 512KB)")
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        requireOrganization(user);
        validateFile(file);
        imageService.saveOrganizationImage(
                user.getOrganization().getId(),
                readBytes(file),
                file.getContentType());
        return "redirect:/settings";
    }

    @Operation(summary = "Upload user image",
            description = "Uploads and replaces the"
                    + " current user's profile image."
                    + " Image is resized to 128x128px."
                    + " Max file size: 512KB.",
            responses = {
                    @ApiResponse(responseCode = "302",
                            description = "Redirect to"
                                    + " /settings on"
                                    + " success"),
                    @ApiResponse(responseCode = "200",
                            description = "Error if no"
                                    + " file selected")})
    @PutMapping("/settings/user-image")
    public String uploadUserImage(
            @Parameter(description = "Image file"
                    + " (PNG, JPG, GIF; max 512KB)")
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        validateFile(file);
        imageService.saveUserImage(
                user.getId(),
                readBytes(file),
                file.getContentType());
        return "redirect:/settings";
    }

    @Operation(summary = "Get organization image",
            description = "Returns the organization's"
                    + " profile image as binary data"
                    + " with its original content type."
                    + " Cached for 1 hour.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Image binary"
                                    + " data",
                            content = @Content(
                                    mediaType = "image"
                                            + "/*")),
                    @ApiResponse(responseCode = "404",
                            description = "No image"
                                    + " set")})
    @GetMapping("/profile-image/org")
    public ResponseEntity<byte[]> orgImage(
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        Organization org = user.getOrganization();
        if (org == null || !org.hasProfileImage()) {
            return ResponseEntity.notFound().build();
        }
        return imageResponse(
                org.getProfileImage(),
                org.getProfileImageType());
    }

    @Operation(summary = "Get user image",
            description = "Returns the current user's"
                    + " profile image as binary data"
                    + " with its original content type."
                    + " Cached for 1 hour.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Image binary"
                                    + " data",
                            content = @Content(
                                    mediaType = "image"
                                            + "/*")),
                    @ApiResponse(responseCode = "404",
                            description = "No image"
                                    + " set")})
    @GetMapping("/profile-image/user")
    public ResponseEntity<byte[]> userImage(
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        if (!user.hasProfileImage()) {
            return ResponseEntity.notFound().build();
        }
        return imageResponse(
                user.getProfileImage(),
                user.getProfileImageType());
    }

    private void requireOrganization(AppUser user) {
        if (!user.hasOrganization()) {
            throw new IllegalArgumentException(
                    "No organization assigned");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    "No file selected");
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to read file", e);
        }
    }

    private ResponseEntity<byte[]> imageResponse(
            byte[] data, String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType(contentType));
        headers.setCacheControl("max-age=3600");
        return new ResponseEntity<>(
                data, headers, HttpStatus.OK);
    }
}

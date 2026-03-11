package solutions.mystuff.application.web;

import java.security.Principal;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Organization;
import solutions.mystuff.domain.port.in.ProfileImageUpload;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SettingsController {

    private final ControllerHelper helper;
    private final ProfileImageUpload imageService;

    public SettingsController(
            ControllerHelper helper,
            ProfileImageUpload imageService) {
        this.helper = helper;
        this.imageService = imageService;
    }

    @GetMapping("/settings")
    public String settings(
            Principal principal, Model model) {
        AppUser user = helper.resolveUser(principal);
        helper.addUserAttrs(user, model);
        model.addAttribute("user", user);
        return "settings";
    }

    @PostMapping("/settings/org-image")
    public String uploadOrgImage(
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        AppUser user = helper.resolveUser(principal);
        validateFile(file);
        imageService.saveOrganizationImage(
                user.getOrganization().getId(),
                readBytes(file),
                file.getContentType());
        return "redirect:/settings";
    }

    @PostMapping("/settings/user-image")
    public String uploadUserImage(
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

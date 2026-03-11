package solutions.mystuff.domain.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Organization;
import solutions.mystuff.domain.port.in.ProfileImageUpload;
import solutions.mystuff.domain.port.out.AppUserRepository;
import solutions.mystuff.domain.port.out
        .OrganizationRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileImageServiceImpl
        implements ProfileImageUpload {

    static final int MAX_SIZE = 128;
    static final int MAX_BYTES = 512 * 1024;

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/png", "image/jpeg");

    private final OrganizationRepository orgRepo;
    private final AppUserRepository userRepo;

    public ProfileImageServiceImpl(
            OrganizationRepository orgRepo,
            AppUserRepository userRepo) {
        this.orgRepo = orgRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void saveOrganizationImage(
            UUID orgId, byte[] imageData,
            String contentType) {
        validateType(contentType);
        byte[] resized = resizeImage(
                imageData, contentType);
        Organization org = orgRepo.findById(orgId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Organization not found"));
        org.setProfileImage(resized);
        org.setProfileImageType(contentType);
        orgRepo.save(org);
    }

    @Override
    public void saveUserImage(
            UUID userId, byte[] imageData,
            String contentType) {
        validateType(contentType);
        byte[] resized = resizeImage(
                imageData, contentType);
        AppUser user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));
        user.setProfileImage(resized);
        user.setProfileImageType(contentType);
        userRepo.save(user);
    }

    @Override
    public byte[] resizeImage(
            byte[] imageData, String contentType) {
        validateType(contentType);
        if (imageData.length > MAX_BYTES) {
            throw new IllegalArgumentException(
                    "Image exceeds maximum size of "
                            + MAX_BYTES / 1024 + "KB");
        }
        try {
            BufferedImage original = ImageIO.read(
                    new ByteArrayInputStream(imageData));
            if (original == null) {
                throw new IllegalArgumentException(
                        "Cannot read image data");
            }
            if (original.getWidth() <= MAX_SIZE
                    && original.getHeight() <= MAX_SIZE) {
                return imageData;
            }
            BufferedImage scaled = scaleImage(original);
            return writeImage(scaled, contentType);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed to process image", e);
        }
    }

    private void validateType(String contentType) {
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Only PNG and JPEG images "
                            + "are supported");
        }
    }

    private BufferedImage scaleImage(
            BufferedImage original) {
        int w = original.getWidth();
        int h = original.getHeight();
        double scale = Math.min(
                (double) MAX_SIZE / w,
                (double) MAX_SIZE / h);
        int newW = (int) (w * scale);
        int newH = (int) (h * scale);
        BufferedImage scaled = new BufferedImage(
                newW, newH, original.getType() != 0
                        ? original.getType()
                        : BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints
                        .VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newW, newH, null);
        g.dispose();
        return scaled;
    }

    private byte[] writeImage(
            BufferedImage image, String contentType)
            throws IOException {
        String format = contentType.equals("image/png")
                ? "png" : "jpg";
        ByteArrayOutputStream out =
                new ByteArrayOutputStream();
        ImageIO.write(image, format, out);
        return out.toByteArray();
    }
}

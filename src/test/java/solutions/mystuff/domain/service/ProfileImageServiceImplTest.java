package solutions.mystuff.domain.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.Organization;
import solutions.mystuff.domain.model.UuidV7;
import solutions.mystuff.domain.port.out.AppUserRepository;
import solutions.mystuff.domain.port.out
        .OrganizationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions
        .assertArrayEquals;
import static org.junit.jupiter.api.Assertions
        .assertEquals;
import static org.junit.jupiter.api.Assertions
        .assertThrows;
import static org.junit.jupiter.api.Assertions
        .assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ProfileImageServiceImpl")
class ProfileImageServiceImplTest {

    private final OrganizationRepository orgRepo =
            mock(OrganizationRepository.class);
    private final AppUserRepository userRepo =
            mock(AppUserRepository.class);
    private final ProfileImageServiceImpl service =
            new ProfileImageServiceImpl(
                    orgRepo, userRepo);

    @Test
    @DisplayName("should save org image")
    void shouldSaveOrgImage() throws Exception {
        UUID orgId = UuidV7.generate();
        Organization org = new Organization();
        org.setId(orgId);
        org.setName("Test");
        when(orgRepo.findById(orgId))
                .thenReturn(Optional.of(org));
        when(orgRepo.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        byte[] png = smallPng(64, 64);
        service.saveOrganizationImage(
                orgId, png, "image/png");

        verify(orgRepo).save(org);
        assertTrue(org.hasProfileImage());
        assertEquals("image/png",
                org.getProfileImageType());
    }

    @Test
    @DisplayName("should save user image")
    void shouldSaveUserImage() throws Exception {
        UUID userId = UuidV7.generate();
        AppUser user = new AppUser(userId, "test");
        when(userRepo.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepo.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        byte[] png = smallPng(64, 64);
        service.saveUserImage(
                userId, png, "image/png");

        verify(userRepo).save(user);
        assertTrue(user.hasProfileImage());
    }

    @Test
    @DisplayName("should resize large image")
    void shouldResizeLargeImage() throws Exception {
        byte[] large = smallPng(256, 256);
        byte[] result = service.resizeImage(
                large, "image/png");

        BufferedImage img = ImageIO.read(
                new java.io.ByteArrayInputStream(result));
        assertTrue(img.getWidth() <= 128);
        assertTrue(img.getHeight() <= 128);
    }

    @Test
    @DisplayName("should not resize small image")
    void shouldNotResizeSmallImage() throws Exception {
        byte[] small = smallPng(64, 64);
        byte[] result = service.resizeImage(
                small, "image/png");
        assertArrayEquals(small, result);
    }

    @Test
    @DisplayName("should reject invalid content type")
    void shouldRejectInvalidType() {
        assertThrows(IllegalArgumentException.class,
                () -> service.resizeImage(
                        new byte[10], "image/gif"));
    }

    @Test
    @DisplayName("should reject oversized file")
    void shouldRejectOversizedFile() {
        byte[] big = new byte[
                ProfileImageServiceImpl.MAX_BYTES + 1];
        assertThrows(IllegalArgumentException.class,
                () -> service.resizeImage(
                        big, "image/png"));
    }

    @Test
    @DisplayName("should reject unreadable data")
    void shouldRejectUnreadableData() {
        assertThrows(IllegalArgumentException.class,
                () -> service.resizeImage(
                        new byte[100], "image/png"));
    }

    @Test
    @DisplayName("should throw when org not found")
    void shouldThrowWhenOrgNotFound() {
        UUID orgId = UuidV7.generate();
        when(orgRepo.findById(orgId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.saveOrganizationImage(
                        orgId, smallPng(64, 64),
                        "image/png"));
    }

    @Test
    @DisplayName("should throw when user not found")
    void shouldThrowWhenUserNotFound() {
        UUID userId = UuidV7.generate();
        when(userRepo.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.saveUserImage(
                        userId, smallPng(64, 64),
                        "image/png"));
    }

    @Test
    @DisplayName("should accept JPEG images")
    void shouldAcceptJpeg() throws Exception {
        byte[] jpeg = smallJpeg(64, 64);
        byte[] result = service.resizeImage(
                jpeg, "image/jpeg");
        assertTrue(result.length > 0);
    }

    private byte[] smallPng(int w, int h)
            throws Exception {
        BufferedImage img = new BufferedImage(
                w, h, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream out =
                new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }

    private byte[] smallJpeg(int w, int h)
            throws Exception {
        BufferedImage img = new BufferedImage(
                w, h, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out =
                new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", out);
        return out.toByteArray();
    }
}

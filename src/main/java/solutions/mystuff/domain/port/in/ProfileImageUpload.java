package solutions.mystuff.domain.port.in;

import java.util.UUID;

public interface ProfileImageUpload {

    void saveOrganizationImage(
            UUID orgId, byte[] imageData,
            String contentType);

    void saveUserImage(
            UUID userId, byte[] imageData,
            String contentType);

    byte[] resizeImage(
            byte[] imageData, String contentType);
}

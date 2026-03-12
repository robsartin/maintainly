package solutions.mystuff.domain.port.in;

import java.util.UUID;

/**
 * Inbound port for uploading profile images.
 *
 * <div class="mermaid">
 * classDiagram
 *     class ProfileImageUpload {
 *         +saveOrganizationImage(UUID, byte[], String) void
 *         +saveUserImage(UUID, byte[], String) void
 *     }
 *     ProfileImageServiceImpl ..|> ProfileImageUpload
 * </div>
 *
 * @see solutions.mystuff.domain.model.Organization
 * @see solutions.mystuff.domain.model.AppUser
 */
public interface ProfileImageUpload {

    /** Save a profile image for the given organization. */
    void saveOrganizationImage(
            UUID orgId, byte[] imageData,
            String contentType);

    /** Save a profile image for the given user. */
    void saveUserImage(
            UUID userId, byte[] imageData,
            String contentType);
}

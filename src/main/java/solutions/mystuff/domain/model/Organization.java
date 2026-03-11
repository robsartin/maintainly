package solutions.mystuff.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Tenant organization that owns items, vendors, and schedules.
 *
 * <p>Stores branding details (name, logo URL) and an optional
 * profile image stored as a byte array with its MIME type.
 *
 * <pre>{@code
 * classDiagram
 *     class Organization {
 *         String name
 *         String logoUrl
 *         byte[] profileImage
 *         String profileImageType
 *         +hasProfileImage() boolean
 *     }
 *     class AppUser {
 *         String username
 *     }
 *     AppUser "*" --> "1" Organization
 * }</pre>
 *
 * @see AppUser
 * @see BaseEntity
 */
@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    public Organization() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(name = "profile_image_type", length = 50)
    private String profileImageType;

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImageType() {
        return profileImageType;
    }

    public void setProfileImageType(String profileImageType) {
        this.profileImageType = profileImageType;
    }

    /** Return true if a profile image has been uploaded. */
    public boolean hasProfileImage() {
        return profileImage != null
                && profileImage.length > 0;
    }
}

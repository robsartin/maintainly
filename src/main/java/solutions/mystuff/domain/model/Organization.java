package solutions.mystuff.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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

    public boolean hasProfileImage() {
        return profileImage != null
                && profileImage.length > 0;
    }
}

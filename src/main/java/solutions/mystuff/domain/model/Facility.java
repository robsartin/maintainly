package solutions.mystuff.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Physical property or building owned by an organization.
 *
 * <p>Groups related {@link Item}s by location, enabling
 * facility-level views of maintenance activity.
 *
 * <div class="mermaid">
 * classDiagram
 *     class OrgOwnedEntity {
 *         UUID organizationId
 *     }
 *     class Facility {
 *         String name
 *         String addressLine1
 *         String addressLine2
 *         String city
 *         String stateProvince
 *         String postalCode
 *         String country
 *         boolean active
 *     }
 *     Facility --|&gt; OrgOwnedEntity
 *     Facility "1" --&gt; "*" Item
 * </div>
 *
 * @see Item
 * @see OrgOwnedEntity
 */
@Entity
@Table(name = "facilities")
public class Facility extends OrgOwnedEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "address_line_1", length = 200)
    private String addressLine1;

    @Column(name = "address_line_2", length = 200)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Column(name = "postal_code", length = 30)
    private String postalCode;

    @Column(length = 100)
    private String country;

    @Column(nullable = false)
    private boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(
            String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /** Returns true if this facility is active. */
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

package solutions.mystuff.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Service provider or contractor associated with an organization.
 *
 * <p>Stores contact information including a primary phone, email,
 * full mailing address, and a list of alternate phone numbers.
 *
 * <div class="mermaid">
 * classDiagram
 *     class OrgOwnedEntity {
 *         UUID organizationId
 *     }
 *     class Vendor {
 *         String name
 *         String phone
 *         String email
 *         String addressLine1
 *         String city
 *     }
 *     class VendorAltPhone {
 *         String phone
 *         String label
 *     }
 *     Vendor --|> OrgOwnedEntity
 *     Vendor "1" --> "*" VendorAltPhone
 * </div>
 *
 * @see VendorAltPhone
 * @see OrgOwnedEntity
 */
@Entity
@Table(name = "vendors")
public class Vendor extends OrgOwnedEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 50)
    private String phone;

    @Column(length = 320)
    private String email;

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

    @Column(length = 2000)
    private String website;

    @Column(length = 2000)
    private String notes;

    @Column(name = "system_managed",
            nullable = false)
    private boolean systemManaged;

    @OneToMany(mappedBy = "vendor",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<VendorAltPhone> altPhones =
            new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setStateProvince(String stateProvince) {
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /** Returns true if this vendor is system-managed and cannot be edited. */
    public boolean isSystemManaged() {
        return systemManaged;
    }

    public void setSystemManaged(boolean systemManaged) {
        this.systemManaged = systemManaged;
    }

    public List<VendorAltPhone> getAltPhones() {
        return altPhones;
    }

    public void setAltPhones(
            List<VendorAltPhone> altPhones) {
        this.altPhones = altPhones;
    }
}

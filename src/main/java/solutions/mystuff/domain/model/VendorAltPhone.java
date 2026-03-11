package solutions.mystuff.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Additional phone number belonging to a {@link Vendor}.
 *
 * <p>Each alternate phone carries an optional label (e.g. "mobile",
 * "after-hours") and is cascade-managed by its parent vendor.
 *
 * <div class="mermaid">
 * classDiagram
 *     class Vendor {
 *         String name
 *         String phone
 *     }
 *     class VendorAltPhone {
 *         String phone
 *         String label
 *     }
 *     Vendor "1" --> "*" VendorAltPhone
 * </div>
 *
 * @see Vendor
 * @see OrgOwnedEntity
 */
@Entity
@Table(name = "vendor_alt_phones")
public class VendorAltPhone extends OrgOwnedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(length = 50)
    private String label;

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

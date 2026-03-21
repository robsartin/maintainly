package solutions.mystuff.domain.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Abstract entity that adds organization ownership for multi-tenancy.
 *
 * <p>Every organization-scoped entity carries an {@code organizationId}
 * foreign key so that queries can be filtered by tenant.
 *
 * <div class="mermaid">
 * classDiagram
 *     class BaseEntity {
 *         UUID id
 *         Instant createdAt
 *         Instant updatedAt
 *     }
 *     class OrgOwnedEntity {
 *         UUID organizationId
 *     }
 *     class Item
 *     class Vendor
 *     class ServiceSchedule
 *     class ServiceRecord
 *     class VendorAltPhone
 *     class Facility
 *     OrgOwnedEntity --|> BaseEntity
 *     Item --|> OrgOwnedEntity
 *     Vendor --|> OrgOwnedEntity
 *     ServiceSchedule --|> OrgOwnedEntity
 *     ServiceRecord --|> OrgOwnedEntity
 *     VendorAltPhone --|> OrgOwnedEntity
 *     Facility --|> OrgOwnedEntity
 * </div>
 *
 * @see BaseEntity
 */
@MappedSuperclass
public abstract class OrgOwnedEntity extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    /** Returns true if this entity belongs to the given organization. */
    public boolean belongsTo(UUID orgId) {
        return organizationId != null
                && organizationId.equals(orgId);
    }
}

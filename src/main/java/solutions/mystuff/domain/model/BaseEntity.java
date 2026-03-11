package solutions.mystuff.domain.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Abstract JPA superclass providing identity and audit timestamps.
 *
 * <p>Every persistent entity inherits a UUIDv7 primary key that is
 * auto-generated on first persist, plus {@code createdAt} and
 * {@code updatedAt} timestamps managed by JPA lifecycle callbacks.
 *
 * <div class="mermaid">
 * classDiagram
 *     class BaseEntity {
 *         UUID id
 *         Instant createdAt
 *         Instant updatedAt
 *         #onCreate()
 *         #onUpdate()
 *     }
 *     class OrgOwnedEntity
 *     class Organization
 *     class AppUser
 *     BaseEntity <|-- OrgOwnedEntity
 *     BaseEntity <|-- Organization
 *     BaseEntity <|-- AppUser
 * </div>
 *
 * @see OrgOwnedEntity
 * @see UuidV7
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "created_at", nullable = false,
            updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Assign a UUIDv7 id and set audit timestamps on persist. */
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UuidV7.generate();
        }
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** Refresh the updatedAt timestamp before each update. */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

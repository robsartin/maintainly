package solutions.mystuff.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Named location or building within an organization.
 *
 * <p>Items may optionally be assigned to a facility so that
 * dashboard statistics can be scoped per-facility.
 *
 * <div class="mermaid">
 * classDiagram
 *     class Facility {
 *         String name
 *     }
 *     Facility --|> OrgOwnedEntity
 *     Item "*" --> "0..1" Facility
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

    public Facility() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

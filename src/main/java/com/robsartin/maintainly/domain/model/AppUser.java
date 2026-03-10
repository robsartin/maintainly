package com.robsartin.maintainly.domain.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class AppUser extends BaseEntity {

    private String username;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    public AppUser() {
    }

    public AppUser(UUID id, String username) {
        setId(id);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public boolean hasOrganization() {
        return organization != null
                && organization.getId() != null;
    }
}

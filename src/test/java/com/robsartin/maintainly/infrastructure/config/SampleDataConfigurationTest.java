package com.robsartin.maintainly.infrastructure.config;

import java.util.Optional;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.model.Organization;
import com.robsartin.maintainly.domain.port.out.AppUserRepository;
import com.robsartin.maintainly.domain.port.out.OrganizationRepository;
import com.robsartin.maintainly.domain.port.out.PropertyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SampleDataConfiguration")
class SampleDataConfigurationTest {

    @Test
    @DisplayName("should load sample data when DB is empty")
    void shouldLoadWhenEmpty() throws Exception {
        OrganizationRepository orgRepo =
                mock(OrganizationRepository.class);
        AppUserRepository userRepo =
                mock(AppUserRepository.class);
        PropertyRepository propRepo =
                mock(PropertyRepository.class);
        Organization org = new Organization();
        org.setId(1);
        org.setName("Test Org");
        when(orgRepo.findById(1))
                .thenReturn(Optional.empty());
        when(orgRepo.save(any(Organization.class)))
                .thenReturn(org);
        AppUser dev = new AppUser(UUID.randomUUID(), "dev");
        when(userRepo.findByUsername("dev"))
                .thenReturn(Optional.of(dev));
        when(userRepo.save(any(AppUser.class)))
                .thenReturn(dev);
        SampleDataConfiguration config =
                new SampleDataConfiguration();
        config.loadSampleData(orgRepo, userRepo, propRepo)
                .run();
        verify(orgRepo).save(any(Organization.class));
        verify(propRepo, atLeastOnce()).save(any());
    }

    @Test
    @DisplayName("should skip when data already exists")
    void shouldSkipWhenExists() throws Exception {
        OrganizationRepository orgRepo =
                mock(OrganizationRepository.class);
        AppUserRepository userRepo =
                mock(AppUserRepository.class);
        PropertyRepository propRepo =
                mock(PropertyRepository.class);
        Organization existing = new Organization();
        existing.setId(1);
        when(orgRepo.findById(1))
                .thenReturn(Optional.of(existing));
        SampleDataConfiguration config =
                new SampleDataConfiguration();
        config.loadSampleData(orgRepo, userRepo, propRepo)
                .run();
        verify(propRepo, never()).save(any());
    }

    @Test
    @DisplayName("should create dev user when not found")
    void shouldCreateDevUser() throws Exception {
        OrganizationRepository orgRepo =
                mock(OrganizationRepository.class);
        AppUserRepository userRepo =
                mock(AppUserRepository.class);
        PropertyRepository propRepo =
                mock(PropertyRepository.class);
        Organization org = new Organization();
        org.setId(1);
        when(orgRepo.findById(1))
                .thenReturn(Optional.empty());
        when(orgRepo.save(any(Organization.class)))
                .thenReturn(org);
        AppUser dev = new AppUser(UUID.randomUUID(), "dev");
        when(userRepo.findByUsername("dev"))
                .thenReturn(Optional.empty());
        when(userRepo.save(any(AppUser.class)))
                .thenReturn(dev);
        SampleDataConfiguration config =
                new SampleDataConfiguration();
        config.loadSampleData(orgRepo, userRepo, propRepo)
                .run();
        verify(userRepo, atLeastOnce())
                .save(any(AppUser.class));
    }
}

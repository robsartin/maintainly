package com.robsartin.maintainly.infrastructure.config;

import java.time.LocalDate;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.model.Organization;
import com.robsartin.maintainly.domain.model.Property;
import com.robsartin.maintainly.domain.model.UuidV7;
import com.robsartin.maintainly.domain.port.out.AppUserRepository;
import com.robsartin.maintainly.domain.port.out.OrganizationRepository;
import com.robsartin.maintainly.domain.port.out.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleDataConfiguration {

    static final UUID SAMPLE_ORG_ID = UuidV7.generate();

    private static final Logger log =
            LoggerFactory.getLogger(
                    SampleDataConfiguration.class);

    @Bean
    public CommandLineRunner loadSampleData(
            OrganizationRepository orgRepo,
            AppUserRepository userRepo,
            PropertyRepository propRepo) {
        return args -> {
            if (orgRepo.findById(SAMPLE_ORG_ID).isPresent()) {
                log.info("Sample data already loaded");
                return;
            }
            log.info("Loading sample data");
            Organization org = new Organization();
            org.setId(SAMPLE_ORG_ID);
            org.setName("Test Org");
            org = orgRepo.save(org);

            AppUser dev = userRepo.findByUsername("dev")
                    .orElseGet(() -> {
                        AppUser u = new AppUser(
                                UuidV7.generate(), "dev");
                        return userRepo.save(u);
                    });
            dev.setOrganization(org);
            userRepo.save(dev);

            log.info("Created sample org {}",
                    org.getId());
            createProperty(propRepo, org.getId(),
                    "123 Main St",
                    "123 Main St, Springfield",
                    LocalDate.now().plusDays(3));
            createProperty(propRepo, org.getId(),
                    "456 Oak Ave",
                    "456 Oak Ave, Shelbyville",
                    LocalDate.now().plusDays(10));
            createProperty(propRepo, org.getId(),
                    "789 Pine Rd",
                    "789 Pine Rd, Capital City",
                    LocalDate.now().minusDays(2));
        };
    }

    private void createProperty(
            PropertyRepository repo, UUID orgId,
            String name, String address,
            LocalDate nextService) {
        Property p = new Property();
        p.setId(UuidV7.generate());
        p.setName(name);
        p.setAddress(address);
        p.setNextServiceDate(nextService);
        p.setOrganizationId(orgId);
        repo.save(p);
    }
}

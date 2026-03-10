package com.robsartin.maintainly.infrastructure.config;

import java.time.LocalDate;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.AppUser;
import com.robsartin.maintainly.domain.model.FrequencyUnit;
import com.robsartin.maintainly.domain.model.Item;
import com.robsartin.maintainly.domain.model.Organization;
import com.robsartin.maintainly.domain.model.ServiceSchedule;
import com.robsartin.maintainly.domain.model.ServiceType;
import com.robsartin.maintainly.domain.model.UuidV7;
import com.robsartin.maintainly.domain.model.Vendor;
import com.robsartin.maintainly.domain.port.out.AppUserRepository;
import com.robsartin.maintainly.domain.port.out.ItemRepository;
import com.robsartin.maintainly.domain.port.out.OrganizationRepository;
import com.robsartin.maintainly.domain.port.out.ServiceScheduleRepository;
import com.robsartin.maintainly.domain.port.out.ServiceTypeRepository;
import com.robsartin.maintainly.domain.port.out.VendorRepository;
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
            ItemRepository itemRepo,
            ServiceTypeRepository typeRepo,
            VendorRepository vendorRepo,
            ServiceScheduleRepository scheduleRepo) {
        return args -> {
            if (orgRepo.findById(SAMPLE_ORG_ID)
                    .isPresent()) {
                log.info("Sample data already loaded");
                return;
            }
            log.info("Loading sample data");
            Organization org = createOrg(orgRepo);
            assignDevUser(userRepo, org);
            createSampleEntities(itemRepo, typeRepo,
                    vendorRepo, scheduleRepo,
                    org.getId());
            log.info("Created sample org {}",
                    org.getId());
        };
    }

    private Organization createOrg(
            OrganizationRepository orgRepo) {
        Organization org = new Organization();
        org.setId(SAMPLE_ORG_ID);
        org.setName("Test Org");
        org.setLogoUrl("/images/sample-logo.png");
        return orgRepo.save(org);
    }

    private void assignDevUser(
            AppUserRepository userRepo,
            Organization org) {
        AppUser dev = userRepo.findByUsername("dev")
                .orElseGet(() -> {
                    AppUser u = new AppUser(
                            UuidV7.generate(), "dev");
                    return userRepo.save(u);
                });
        dev.setOrganization(org);
        userRepo.save(dev);
    }

    private void createSampleEntities(
            ItemRepository itemRepo,
            ServiceTypeRepository typeRepo,
            VendorRepository vendorRepo,
            ServiceScheduleRepository scheduleRepo,
            UUID orgId) {
        ServiceType hvac = createServiceType(
                typeRepo, orgId,
                "HVAC_INSPECTION", "HVAC Inspection");
        ServiceType plumbing = createServiceType(
                typeRepo, orgId,
                "PLUMBING_CHECK", "Plumbing Check");
        Vendor vendor = createVendor(vendorRepo, orgId);
        Item furnace = createItem(itemRepo, orgId,
                "Main Furnace", "Basement",
                "Carrier", "58STA", 2020);
        Item waterHeater = createItem(itemRepo, orgId,
                "Water Heater", "Utility Room",
                "Rheem", "PROG50", 2021);
        createSchedule(scheduleRepo, orgId,
                furnace, hvac, vendor,
                FrequencyUnit.months, 6,
                LocalDate.now().plusDays(3));
        createSchedule(scheduleRepo, orgId,
                waterHeater, plumbing, null,
                FrequencyUnit.years, 1,
                LocalDate.now().minusDays(2));
    }

    private Vendor createVendor(
            VendorRepository vendorRepo, UUID orgId) {
        Vendor vendor = new Vendor();
        vendor.setId(UuidV7.generate());
        vendor.setOrganizationId(orgId);
        vendor.setName("ABC Maintenance");
        vendor.setPhone("555-0100");
        return vendorRepo.save(vendor);
    }

    private ServiceType createServiceType(
            ServiceTypeRepository repo, UUID orgId,
            String code, String name) {
        ServiceType st = new ServiceType();
        st.setId(UuidV7.generate());
        st.setOrganizationId(orgId);
        st.setCode(code);
        st.setName(name);
        return repo.save(st);
    }

    private Item createItem(
            ItemRepository repo, UUID orgId,
            String name, String location,
            String manufacturer, String model,
            int year) {
        Item item = new Item();
        item.setId(UuidV7.generate());
        item.setOrganizationId(orgId);
        item.setName(name);
        item.setLocation(location);
        item.setManufacturer(manufacturer);
        item.setModelName(model);
        item.setModelYear(year);
        return repo.save(item);
    }

    private void createSchedule(
            ServiceScheduleRepository repo, UUID orgId,
            Item item, ServiceType type,
            Vendor vendor, FrequencyUnit unit,
            int interval, LocalDate nextDue) {
        ServiceSchedule s = new ServiceSchedule();
        s.setId(UuidV7.generate());
        s.setOrganizationId(orgId);
        s.setItem(item);
        s.setServiceType(type);
        s.setPreferredVendor(vendor);
        s.setFrequencyUnit(unit);
        s.setFrequencyInterval(interval);
        s.setFirstDueDate(nextDue);
        s.setNextDueDate(nextDue);
        repo.save(s);
    }
}

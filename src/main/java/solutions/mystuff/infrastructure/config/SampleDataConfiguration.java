package solutions.mystuff.infrastructure.config;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.AppUser;
import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.Organization;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.UuidV7;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.out.AppUserRepository;
import solutions.mystuff.domain.port.out.ItemRepository;
import solutions.mystuff.domain.port.out.OrganizationRepository;
import solutions.mystuff.domain.port.out.ServiceScheduleRepository;
import solutions.mystuff.domain.port.out.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seeds the database with sample data on startup when absent.
 *
 * <div class="mermaid">
 * flowchart TD
 *     A[CommandLineRunner] --> B{org exists?}
 *     B -->|yes| C[skip]
 *     B -->|no| D[create Organization]
 *     D --> E[create dev AppUser]
 *     E --> F[create Items, Vendors, Schedules]
 * </div>
 *
 * @see CommandLineRunner
 */
@Configuration
public class SampleDataConfiguration {

    static final UUID SAMPLE_ORG_ID = UuidV7.generate();

    private static final Logger log =
            LoggerFactory.getLogger(
                    SampleDataConfiguration.class);

    /** Returns a runner that seeds sample data if not already present. */
    @Bean
    public CommandLineRunner loadSampleData(
            OrganizationRepository orgRepo,
            AppUserRepository userRepo,
            ItemRepository itemRepo,
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
            createSampleEntities(itemRepo,
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
        org.setLogoUrl("/images/sample-logo.svg");
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
            VendorRepository vendorRepo,
            ServiceScheduleRepository scheduleRepo,
            UUID orgId) {
        String hvac = "HVAC Inspection";
        String plumb = "Plumbing Check";
        String elec = "Electrical Inspection";
        String filter = "Filter Replacement";
        String gen = "General Maintenance";
        Vendor abc = createVendor(vendorRepo, orgId,
                "ABC Maintenance", "555-0100");
        Vendor quick = createVendor(vendorRepo, orgId,
                "QuickFix Services", "555-0200");
        createOriginalItems(itemRepo, scheduleRepo,
                orgId, hvac, plumb, abc);
        createAdditionalItems(itemRepo, scheduleRepo,
                orgId, hvac, plumb, elec, filter, gen,
                abc, quick);
    }

    private void createOriginalItems(
            ItemRepository itemRepo,
            ServiceScheduleRepository scheduleRepo,
            UUID orgId, String hvac,
            String plumb, Vendor abc) {
        Item furnace = createItem(itemRepo, orgId,
                "Main Furnace", "Basement",
                "Carrier", "58STA", 2020,
                "CR-58STA-20-4471");
        Item waterHeater = createItem(itemRepo, orgId,
                "Water Heater", "Utility Room",
                "Rheem", "PROG50", 2021,
                "RH-P50-21-8832");
        sched(scheduleRepo, orgId, furnace, hvac, abc,
                FrequencyUnit.months, 6, 3);
        sched(scheduleRepo, orgId, waterHeater, plumb,
                null, FrequencyUnit.years, 1, -2);
    }

    private void createAdditionalItems(
            ItemRepository itemRepo,
            ServiceScheduleRepository scheduleRepo,
            UUID orgId, String hvac,
            String plumb, String elec,
            String filter, String gen,
            Vendor abc, Vendor quick) {
        createMechanicalItems(itemRepo, scheduleRepo,
                orgId, hvac, plumb, elec, filter, gen,
                abc, quick);
        createBuildingItems(itemRepo, scheduleRepo,
                orgId, hvac, plumb, elec, filter, gen,
                abc, quick);
    }

    private void createMechanicalItems(
            ItemRepository ir,
            ServiceScheduleRepository sr,
            UUID o, String hvac,
            String plumb, String elec,
            String filter, String gen,
            Vendor abc, Vendor quick) {
        Item ac = createItem(ir, o, "Central AC",
                "Roof", "Trane", "XR15", 2019,
                "TR-XR15-19-2201");
        sched(sr, o, ac, hvac, abc,
                FrequencyUnit.months, 6, 10);
        sched(sr, o, ac, filter, null,
                FrequencyUnit.months, 3, -5);
        Item gen1 = createItem(ir, o,
                "Backup Generator", "Parking Garage",
                "Generac", "QT070", 2022,
                "GN-QT-22-1190");
        sched(sr, o, gen1, gen, abc,
                FrequencyUnit.months, 3, 15);
        Item boiler = createItem(ir, o,
                "Steam Boiler", "Basement Mech Room",
                "Weil-McLain", "SVF", 2017,
                "WM-SVF-17-0098");
        sched(sr, o, boiler, hvac, abc,
                FrequencyUnit.years, 1, -30);
        sched(sr, o, boiler, gen, null,
                FrequencyUnit.months, 6, 45);
    }

    private void createBuildingItems(
            ItemRepository ir,
            ServiceScheduleRepository sr,
            UUID o, String hvac,
            String plumb, String elec,
            String filter, String gen,
            Vendor abc, Vendor quick) {
        Item pump = createItem(ir, o, "Sump Pump",
                "Basement", "Zoeller", "M53", 2023,
                "ZL-M53-23-6621");
        sched(sr, o, pump, plumb, quick,
                FrequencyUnit.months, 6, 20);
        Item comp = createItem(ir, o,
                "Air Compressor", "Workshop",
                "Ingersoll Rand", "SS3", 2021,
                "IR-SS3-21-4455");
        sched(sr, o, comp, gen, null,
                FrequencyUnit.months, 6, -10);
        sched(sr, o, comp, filter, abc,
                FrequencyUnit.months, 1, 7);
        Item elev = createItem(ir, o,
                "Elevator #1", "Lobby",
                "Otis", "Gen3", 2018, "OT-G3-18-7744");
        sched(sr, o, elev, gen, quick,
                FrequencyUnit.months, 1, 5);
        sched(sr, o, elev, elec, quick,
                FrequencyUnit.years, 1, 60);
        createFacilityItems(ir, sr, o, hvac, plumb,
                elec, filter, gen, abc, quick);
    }

    private void createFacilityItems(
            ItemRepository ir,
            ServiceScheduleRepository sr,
            UUID o, String hvac,
            String plumb, String elec,
            String filter, String gen,
            Vendor abc, Vendor quick) {
        Item panel = createItem(ir, o,
                "Main Electrical Panel", "Utility Room",
                "Square D", "QO142", 2015,
                "SD-QO-15-3300");
        sched(sr, o, panel, elec, quick,
                FrequencyUnit.years, 1, 90);
        Item sprink = createItem(ir, o,
                "Fire Sprinkler System", "Building Wide",
                "Viking", "VK100", 2016,
                "VK-100-16-8877");
        sched(sr, o, sprink, gen, quick,
                FrequencyUnit.years, 1, 120);
        Item roof = createItem(ir, o,
                "Rooftop HVAC Unit", "Roof",
                "Lennox", "LRP14", 2020,
                "LX-LRP-20-5533");
        sched(sr, o, roof, hvac, abc,
                FrequencyUnit.months, 6, -15);
        sched(sr, o, roof, filter, null,
                FrequencyUnit.months, 3, 8);
        Item soft = createItem(ir, o,
                "Water Softener", "Utility Room",
                "Culligan", "HE1.25", 2022,
                "CG-HE-22-9910");
        sched(sr, o, soft, plumb, null,
                FrequencyUnit.months, 6, 30);
    }

    private void sched(
            ServiceScheduleRepository repo, UUID orgId,
            Item item, String type, Vendor vendor,
            FrequencyUnit unit, int interval,
            int daysFromNow) {
        createSchedule(repo, orgId, item, type, vendor,
                unit, interval,
                LocalDate.now().plusDays(daysFromNow));
    }

    private Vendor createVendor(
            VendorRepository vendorRepo, UUID orgId,
            String name, String phone) {
        Vendor vendor = new Vendor();
        vendor.setId(UuidV7.generate());
        vendor.setOrganizationId(orgId);
        vendor.setName(name);
        vendor.setPhone(phone);
        return vendorRepo.save(vendor);
    }

    private Item createItem(
            ItemRepository repo, UUID orgId,
            String name, String location,
            String manufacturer, String model,
            int year, String serialNumber) {
        Item item = new Item();
        item.setId(UuidV7.generate());
        item.setOrganizationId(orgId);
        item.setName(name);
        item.setLocation(location);
        item.setManufacturer(manufacturer);
        item.setModelName(model);
        item.setModelYear(year);
        item.setSerialNumber(serialNumber);
        return repo.save(item);
    }

    private void createSchedule(
            ServiceScheduleRepository repo, UUID orgId,
            Item item, String type,
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

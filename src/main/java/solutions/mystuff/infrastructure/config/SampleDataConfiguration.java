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
            assignUser(userRepo, "dev", org);
            assignUser(userRepo,
                    "rob.sartin@gmail.com", org);
            seed(itemRepo, vendorRepo, scheduleRepo,
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

    private void assignUser(
            AppUserRepository userRepo,
            String username, Organization org) {
        AppUser user = userRepo.findByUsername(username)
                .orElseGet(() -> {
                    AppUser u = new AppUser(
                            UuidV7.generate(), username);
                    return userRepo.save(u);
                });
        user.setOrganization(org);
        userRepo.save(user);
    }

    private void seed(
            ItemRepository itemRepo,
            VendorRepository vendorRepo,
            ServiceScheduleRepository scheduleRepo,
            UUID orgId) {
        Ctx ctx = new Ctx(itemRepo, scheduleRepo, orgId,
                createUnknownVendor(vendorRepo, orgId),
                createVendor(vendorRepo, orgId,
                        "ABC Maintenance", "555-0100"),
                createVendor(vendorRepo, orgId,
                        "QuickFix Services", "555-0200"));
        seedOriginal(ctx);
        seedMechanical(ctx);
        seedBuilding(ctx);
        seedFacility(ctx);
    }

    private void seedOriginal(Ctx c) {
        Item furnace = item(c, "Main Furnace",
                "Basement", "Carrier", "58STA", 2020,
                "CR-58STA-20-4471");
        Item wh = item(c, "Water Heater",
                "Utility Room", "Rheem", "PROG50", 2021,
                "RH-P50-21-8832");
        sched(c, furnace, "HVAC Inspection", c.abc,
                FrequencyUnit.months, 6, 3);
        sched(c, wh, "Plumbing Check", c.unknown,
                FrequencyUnit.years, 1, -2);
    }

    private void seedMechanical(Ctx c) {
        Item ac = item(c, "Central AC", "Roof",
                "Trane", "XR15", 2019,
                "TR-XR15-19-2201");
        sched(c, ac, "HVAC Inspection", c.abc,
                FrequencyUnit.months, 6, 10);
        sched(c, ac, "Filter Replacement", c.unknown,
                FrequencyUnit.months, 3, -5);
        Item gen = item(c, "Backup Generator",
                "Parking Garage", "Generac", "QT070",
                2022, "GN-QT-22-1190");
        sched(c, gen, "General Maintenance", c.abc,
                FrequencyUnit.months, 3, 15);
        Item boiler = item(c, "Steam Boiler",
                "Basement Mech Room", "Weil-McLain",
                "SVF", 2017, "WM-SVF-17-0098");
        sched(c, boiler, "HVAC Inspection", c.abc,
                FrequencyUnit.years, 1, -30);
        sched(c, boiler, "General Maintenance",
                c.unknown, FrequencyUnit.months, 6, 45);
    }

    private void seedBuilding(Ctx c) {
        Item pump = item(c, "Sump Pump", "Basement",
                "Zoeller", "M53", 2023,
                "ZL-M53-23-6621");
        sched(c, pump, "Plumbing Check", c.quick,
                FrequencyUnit.months, 6, 20);
        Item comp = item(c, "Air Compressor",
                "Workshop", "Ingersoll Rand", "SS3",
                2021, "IR-SS3-21-4455");
        sched(c, comp, "General Maintenance",
                c.unknown, FrequencyUnit.months, 6, -10);
        sched(c, comp, "Filter Replacement", c.abc,
                FrequencyUnit.months, 1, 7);
        Item elev = item(c, "Elevator #1", "Lobby",
                "Otis", "Gen3", 2018,
                "OT-G3-18-7744");
        sched(c, elev, "General Maintenance", c.quick,
                FrequencyUnit.months, 1, 5);
        sched(c, elev, "Electrical Inspection",
                c.quick, FrequencyUnit.years, 1, 60);
    }

    private void seedFacility(Ctx c) {
        Item panel = item(c, "Main Electrical Panel",
                "Utility Room", "Square D", "QO142",
                2015, "SD-QO-15-3300");
        sched(c, panel, "Electrical Inspection",
                c.quick, FrequencyUnit.years, 1, 90);
        Item sprink = item(c, "Fire Sprinkler System",
                "Building Wide", "Viking", "VK100",
                2016, "VK-100-16-8877");
        sched(c, sprink, "General Maintenance",
                c.quick, FrequencyUnit.years, 1, 120);
        Item roof = item(c, "Rooftop HVAC Unit",
                "Roof", "Lennox", "LRP14", 2020,
                "LX-LRP-20-5533");
        sched(c, roof, "HVAC Inspection", c.abc,
                FrequencyUnit.months, 6, -15);
        sched(c, roof, "Filter Replacement", c.unknown,
                FrequencyUnit.months, 3, 8);
        Item soft = item(c, "Water Softener",
                "Utility Room", "Culligan", "HE1.25",
                2022, "CG-HE-22-9910");
        sched(c, soft, "Plumbing Check", c.unknown,
                FrequencyUnit.months, 6, 30);
    }

    private Item item(Ctx c, String name,
            String location, String manufacturer,
            String model, int year, String serial) {
        Item i = new Item();
        i.setId(UuidV7.generate());
        i.setOrganizationId(c.orgId);
        i.setName(name);
        i.setLocation(location);
        i.setManufacturer(manufacturer);
        i.setModelName(model);
        i.setModelYear(year);
        i.setSerialNumber(serial);
        return c.items.save(i);
    }

    private void sched(Ctx c, Item item, String type,
            Vendor vendor, FrequencyUnit unit,
            int interval, int daysFromNow) {
        LocalDate due =
                LocalDate.now().plusDays(daysFromNow);
        ServiceSchedule s = new ServiceSchedule();
        s.setId(UuidV7.generate());
        s.setOrganizationId(c.orgId);
        s.setItem(item);
        s.setServiceType(type);
        s.setPreferredVendor(vendor);
        s.setFrequencyUnit(unit);
        s.setFrequencyInterval(interval);
        s.setFirstDueDate(due);
        s.setNextDueDate(due);
        c.schedules.save(s);
    }

    private Vendor createUnknownVendor(
            VendorRepository repo, UUID orgId) {
        Vendor v = new Vendor();
        v.setOrganizationId(orgId);
        v.setName("Unknown Vendor");
        v.setSystemManaged(true);
        return repo.save(v);
    }

    private Vendor createVendor(
            VendorRepository repo, UUID orgId,
            String name, String phone) {
        Vendor v = new Vendor();
        v.setId(UuidV7.generate());
        v.setOrganizationId(orgId);
        v.setName(name);
        v.setPhone(phone);
        return repo.save(v);
    }

    private record Ctx(
            ItemRepository items,
            ServiceScheduleRepository schedules,
            UUID orgId,
            Vendor unknown, Vendor abc, Vendor quick) {
    }
}

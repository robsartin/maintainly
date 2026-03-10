package solutions.mystuff.infrastructure.config;

import java.util.Optional;

import solutions.mystuff.domain.model.AppUser;
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
import org.junit.jupiter.api.BeforeEach;
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

    private final OrganizationRepository orgRepo =
            mock(OrganizationRepository.class);
    private final AppUserRepository userRepo =
            mock(AppUserRepository.class);
    private final ItemRepository itemRepo =
            mock(ItemRepository.class);
    private final VendorRepository vendorRepo =
            mock(VendorRepository.class);
    private final ServiceScheduleRepository scheduleRepo =
            mock(ServiceScheduleRepository.class);

    @BeforeEach
    void stubSavePassthrough() {
        when(vendorRepo.save(any(Vendor.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(scheduleRepo.save(
                any(ServiceSchedule.class)))
                .thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("should load sample data when empty")
    void shouldLoadWhenEmpty() throws Exception {
        stubOrgNotFound();
        stubDevUserExists();
        runLoader();
        verify(orgRepo).save(any(Organization.class));
        verify(itemRepo, atLeastOnce())
                .save(any(Item.class));
        verify(scheduleRepo, atLeastOnce())
                .save(any(ServiceSchedule.class));
    }

    @Test
    @DisplayName("should skip when data exists")
    void shouldSkipWhenExists() throws Exception {
        Organization existing = new Organization();
        existing.setId(
                SampleDataConfiguration.SAMPLE_ORG_ID);
        when(orgRepo.findById(
                SampleDataConfiguration.SAMPLE_ORG_ID))
                .thenReturn(Optional.of(existing));
        runLoader();
        verify(itemRepo, never()).save(any());
    }

    @Test
    @DisplayName("should create dev user when not found")
    void shouldCreateDevUser() throws Exception {
        stubOrgNotFound();
        AppUser dev = new AppUser(
                UuidV7.generate(), "dev");
        when(userRepo.findByUsername("dev"))
                .thenReturn(Optional.empty());
        when(userRepo.save(any(AppUser.class)))
                .thenReturn(dev);
        runLoader();
        verify(userRepo, atLeastOnce())
                .save(any(AppUser.class));
    }

    private void stubOrgNotFound() {
        Organization org = new Organization();
        org.setId(SampleDataConfiguration.SAMPLE_ORG_ID);
        org.setName("Test Org");
        when(orgRepo.findById(
                SampleDataConfiguration.SAMPLE_ORG_ID))
                .thenReturn(Optional.empty());
        when(orgRepo.save(any(Organization.class)))
                .thenReturn(org);
    }

    private void stubDevUserExists() {
        AppUser dev = new AppUser(
                UuidV7.generate(), "dev");
        when(userRepo.findByUsername("dev"))
                .thenReturn(Optional.of(dev));
        when(userRepo.save(any(AppUser.class)))
                .thenReturn(dev);
    }

    private void runLoader() throws Exception {
        new SampleDataConfiguration()
                .loadSampleData(orgRepo, userRepo,
                        itemRepo, vendorRepo,
                        scheduleRepo)
                .run();
    }
}

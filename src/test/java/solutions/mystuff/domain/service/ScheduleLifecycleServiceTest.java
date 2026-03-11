package solutions.mystuff.domain.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.FrequencyUnit;
import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.port.in.RecordCreation;
import solutions.mystuff.domain.port.out.ItemRepository;
import solutions.mystuff.domain.port.out
        .ServiceScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions
        .assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ScheduleLifecycleService")
class ScheduleLifecycleServiceTest {

    private final ServiceScheduleRepository schedRepo =
            mock(ServiceScheduleRepository.class);
    private final ItemRepository itemRepo =
            mock(ItemRepository.class);
    private final RecordCreation recordCreation =
            mock(RecordCreation.class);
    private final ScheduleLifecycleService service =
            new ScheduleLifecycleService(
                    schedRepo, itemRepo,
                    recordCreation);

    private final UUID orgId = UUID.randomUUID();

    @Test
    @DisplayName("should create schedule")
    void shouldCreateSchedule() {
        UUID itemId = UUID.randomUUID();
        Item item = new Item();
        item.setName("Test");
        when(itemRepo.findByIdAndOrganizationId(
                itemId, orgId))
                .thenReturn(Optional.of(item));
        when(schedRepo.save(
                any(ServiceSchedule.class)))
                .thenAnswer(i -> i.getArgument(0));

        ServiceSchedule result =
                service.createSchedule(orgId, itemId,
                        "Filter Change", null,
                        LocalDate.of(2026, 6, 1),
                        3, FrequencyUnit.months);

        assertThat(result.getServiceType())
                .isEqualTo("Filter Change");
        assertThat(result.getFrequencyInterval())
                .isEqualTo(3);
    }

    @Test
    @DisplayName("should reject blank service type")
    void shouldRejectBlankType() {
        assertThatThrownBy(() ->
                service.createSchedule(orgId,
                        UUID.randomUUID(), "  ", null,
                        LocalDate.now(), 1,
                        FrequencyUnit.months))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    @DisplayName("should reject zero interval")
    void shouldRejectZeroInterval() {
        assertThatThrownBy(() ->
                service.createSchedule(orgId,
                        UUID.randomUUID(), "Test", null,
                        LocalDate.now(), 0,
                        FrequencyUnit.months))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("at least 1");
    }

    @Test
    @DisplayName("should complete schedule")
    void shouldCompleteSchedule() {
        UUID schedId = UUID.randomUUID();
        ServiceSchedule sched = buildSchedule();
        when(schedRepo.findByIdAndOrganizationId(
                schedId, orgId))
                .thenReturn(Optional.of(sched));
        when(schedRepo.save(
                any(ServiceSchedule.class)))
                .thenAnswer(i -> i.getArgument(0));

        LocalDate date = LocalDate.of(2026, 3, 10);
        service.completeSchedule(schedId, orgId,
                null, "Done", date, null);

        verify(recordCreation).createRecord(
                eq(orgId), any(), any(), eq(sched),
                eq(null), eq("Done"), eq(date),
                eq(null));
        verify(schedRepo).save(sched);
    }

    @Test
    @DisplayName("should skip schedule")
    void shouldSkipSchedule() {
        UUID schedId = UUID.randomUUID();
        ServiceSchedule sched = buildSchedule();
        sched.setNextDueDate(
                LocalDate.of(2026, 3, 1));
        when(schedRepo.findByIdAndOrganizationId(
                schedId, orgId))
                .thenReturn(Optional.of(sched));
        when(schedRepo.save(
                any(ServiceSchedule.class)))
                .thenAnswer(i -> i.getArgument(0));

        service.skipSchedule(schedId, orgId);

        assertThat(sched.getLastCompletedDate())
                .isNull();
        verify(schedRepo).save(sched);
    }

    @Test
    @DisplayName("should skip with null due date")
    void shouldSkipNullDueDate() {
        UUID schedId = UUID.randomUUID();
        ServiceSchedule sched = buildSchedule();
        sched.setNextDueDate(null);
        when(schedRepo.findByIdAndOrganizationId(
                schedId, orgId))
                .thenReturn(Optional.of(sched));
        when(schedRepo.save(
                any(ServiceSchedule.class)))
                .thenAnswer(i -> i.getArgument(0));

        service.skipSchedule(schedId, orgId);

        verify(schedRepo).save(sched);
    }

    @Test
    @DisplayName("should edit schedule")
    void shouldEditSchedule() {
        UUID schedId = UUID.randomUUID();
        ServiceSchedule sched = buildSchedule();
        when(schedRepo.findByIdAndOrganizationId(
                schedId, orgId))
                .thenReturn(Optional.of(sched));
        when(schedRepo.save(
                any(ServiceSchedule.class)))
                .thenAnswer(i -> i.getArgument(0));

        Vendor vendor = new Vendor();
        vendor.setName("Acme");
        service.editSchedule(schedId, orgId,
                "Updated Type",
                LocalDate.of(2026, 12, 1),
                6, FrequencyUnit.months, vendor);

        assertThat(sched.getServiceType())
                .isEqualTo("Updated Type");
        assertThat(sched.getFrequencyInterval())
                .isEqualTo(6);
        assertThat(sched.getPreferredVendor())
                .isEqualTo(vendor);
    }

    @Test
    @DisplayName("should deactivate schedule")
    void shouldDeactivateSchedule() {
        UUID schedId = UUID.randomUUID();
        ServiceSchedule sched = buildSchedule();
        when(schedRepo.findByIdAndOrganizationId(
                schedId, orgId))
                .thenReturn(Optional.of(sched));
        when(schedRepo.save(
                any(ServiceSchedule.class)))
                .thenAnswer(i -> i.getArgument(0));

        service.deactivateSchedule(schedId, orgId);

        assertThat(sched.isActive()).isFalse();
    }

    @Test
    @DisplayName("should throw when schedule not found")
    void shouldThrowWhenNotFound() {
        UUID schedId = UUID.randomUUID();
        when(schedRepo.findByIdAndOrganizationId(
                schedId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.skipSchedule(schedId, orgId))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage("Schedule not found");
    }

    private ServiceSchedule buildSchedule() {
        ServiceSchedule s = new ServiceSchedule();
        s.setServiceType("Filter Change");
        s.setFrequencyUnit(FrequencyUnit.months);
        s.setFrequencyInterval(3);
        s.setActive(true);
        Item item = new Item();
        item.setName("Test Item");
        s.setItem(item);
        return s;
    }
}

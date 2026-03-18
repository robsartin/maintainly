package solutions.mystuff.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.ServiceCompletion;
import solutions.mystuff.domain.model.ServiceRecord;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.port.out
        .ServiceRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions
        .assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("RecordCreationService")
class RecordCreationServiceTest {

    private final ServiceRecordRepository repo =
            mock(ServiceRecordRepository.class);
    private final RecordCreationService service =
            new RecordCreationService(repo);

    private final UUID orgId = UUID.randomUUID();

    @Test
    @DisplayName("should create record with all fields")
    void shouldCreateRecord() {
        Item item = new Item();
        item.setName("Test Item");
        ServiceSchedule schedule = new ServiceSchedule();
        schedule.setServiceType("Filter Change");
        when(repo.save(any(ServiceRecord.class)))
                .thenAnswer(i -> i.getArgument(0));

        ServiceCompletion completion =
                new ServiceCompletion(null,
                        "Replaced filter",
                        LocalDate.of(2026, 3, 10),
                        "John",
                        new BigDecimal("50.00"));
        service.createRecord(orgId, item,
                schedule, completion);

        ArgumentCaptor<ServiceRecord> captor =
                ArgumentCaptor.forClass(
                        ServiceRecord.class);
        verify(repo).save(captor.capture());
        ServiceRecord saved = captor.getValue();
        assertThat(saved.getSummary())
                .isEqualTo("Replaced filter");
        assertThat(saved.getTechnicianName())
                .isEqualTo("John");
        assertThat(saved.getServiceType())
                .isEqualTo("Filter Change");
    }

    @Test
    @DisplayName("should skip tech when blank")
    void shouldSkipBlankTech() {
        Item item = new Item();
        item.setName("Test");
        when(repo.save(any(ServiceRecord.class)))
                .thenAnswer(i -> i.getArgument(0));

        ServiceCompletion completion =
                new ServiceCompletion(null, "Summary",
                        LocalDate.now(), "  ", null);
        service.createRecord(orgId, item,
                null, completion);

        ArgumentCaptor<ServiceRecord> captor =
                ArgumentCaptor.forClass(
                        ServiceRecord.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getTechnicianName())
                .isNull();
    }

    @Test
    @DisplayName("should reject blank summary")
    void shouldRejectBlankSummary() {
        Item item = new Item();
        ServiceCompletion completion =
                new ServiceCompletion(null, "  ",
                        LocalDate.now(), null, null);
        assertThatThrownBy(() ->
                service.createRecord(orgId, item,
                        null, completion))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    @DisplayName("should reject long summary")
    void shouldRejectLongSummary() {
        Item item = new Item();
        String longSummary = "x".repeat(251);
        ServiceCompletion completion =
                new ServiceCompletion(null,
                        longSummary,
                        LocalDate.now(), null, null);
        assertThatThrownBy(() ->
                service.createRecord(orgId, item,
                        null, completion))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("maximum length");
    }

    @Test
    @DisplayName("should reject long tech name")
    void shouldRejectLongTechName() {
        Item item = new Item();
        String longTech = "x".repeat(201);
        ServiceCompletion completion =
                new ServiceCompletion(null, "Summary",
                        LocalDate.now(), longTech,
                        null);
        assertThatThrownBy(() ->
                service.createRecord(orgId, item,
                        null, completion))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("maximum length");
    }

    @Test
    @DisplayName("should update record fields")
    void shouldUpdateRecordFields() {
        UUID recordId = UUID.randomUUID();
        ServiceRecord existing = new ServiceRecord();
        existing.setSummary("Old summary");
        existing.setServiceDate(LocalDate.of(2026, 1, 1));
        when(repo.findByIdAndOrganizationId(
                recordId, orgId))
                .thenReturn(Optional.of(existing));
        when(repo.save(any(ServiceRecord.class)))
                .thenAnswer(i -> i.getArgument(0));

        service.updateRecord(orgId, recordId,
                "New summary",
                LocalDate.of(2026, 6, 15),
                "Alice",
                new BigDecimal("75.00"));

        ArgumentCaptor<ServiceRecord> captor =
                ArgumentCaptor.forClass(
                        ServiceRecord.class);
        verify(repo).save(captor.capture());
        ServiceRecord saved = captor.getValue();
        assertThat(saved.getSummary())
                .isEqualTo("New summary");
        assertThat(saved.getServiceDate())
                .isEqualTo(LocalDate.of(2026, 6, 15));
        assertThat(saved.getTechnicianName())
                .isEqualTo("Alice");
        assertThat(saved.getCost())
                .isEqualByComparingTo("75.00");
    }

    @Test
    @DisplayName("should throw when updating unknown record")
    void shouldThrowWhenUpdatingUnknownRecord() {
        UUID recordId = UUID.randomUUID();
        when(repo.findByIdAndOrganizationId(
                recordId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateRecord(orgId, recordId,
                        "Summary", LocalDate.now(),
                        null, null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("should reject blank summary on update")
    void shouldRejectBlankSummaryOnUpdate() {
        assertThatThrownBy(() ->
                service.updateRecord(orgId,
                        UUID.randomUUID(), "  ",
                        LocalDate.now(), null, null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    @DisplayName("should delete record after verifying org")
    void shouldDeleteRecordWhenFound() {
        UUID recordId = UUID.randomUUID();
        ServiceRecord existing = new ServiceRecord();
        when(repo.findByIdAndOrganizationId(
                recordId, orgId))
                .thenReturn(Optional.of(existing));

        service.deleteRecord(orgId, recordId);

        verify(repo).deleteByIdAndOrganizationId(
                recordId, orgId);
    }

    @Test
    @DisplayName("should throw when deleting unknown record")
    void shouldThrowWhenDeletingUnknownRecord() {
        UUID recordId = UUID.randomUUID();
        when(repo.findByIdAndOrganizationId(
                recordId, orgId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.deleteRecord(orgId, recordId))
                .isInstanceOf(NotFoundException.class);
    }
}

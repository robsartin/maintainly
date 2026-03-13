package solutions.mystuff.domain.service;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
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
        when(repo.save(any(ServiceRecord.class)))
                .thenAnswer(i -> i.getArgument(0));

        service.createRecord(orgId, item,
                "Filter Change", null, null,
                "Replaced filter",
                LocalDate.of(2026, 3, 10), "John");

        ArgumentCaptor<ServiceRecord> captor =
                ArgumentCaptor.forClass(
                        ServiceRecord.class);
        verify(repo).save(captor.capture());
        ServiceRecord saved = captor.getValue();
        assertThat(saved.getSummary())
                .isEqualTo("Replaced filter");
        assertThat(saved.getTechnicianName())
                .isEqualTo("John");
    }

    @Test
    @DisplayName("should skip tech when blank")
    void shouldSkipBlankTech() {
        Item item = new Item();
        item.setName("Test");
        when(repo.save(any(ServiceRecord.class)))
                .thenAnswer(i -> i.getArgument(0));

        service.createRecord(orgId, item,
                "Test", null, null, "Summary",
                LocalDate.now(), "  ");

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
        assertThatThrownBy(() ->
                service.createRecord(orgId, item,
                        "Test", null, null, "  ",
                        LocalDate.now(), null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("required");
    }

    @Test
    @DisplayName("should reject long summary")
    void shouldRejectLongSummary() {
        Item item = new Item();
        String longSummary = "x".repeat(251);
        assertThatThrownBy(() ->
                service.createRecord(orgId, item,
                        "Test", null, null,
                        longSummary,
                        LocalDate.now(), null))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("maximum length");
    }

    @Test
    @DisplayName("should reject long tech name")
    void shouldRejectLongTechName() {
        Item item = new Item();
        String longTech = "x".repeat(201);
        assertThatThrownBy(() ->
                service.createRecord(orgId, item,
                        "Test", null, null,
                        "Summary",
                        LocalDate.now(), longTech))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessageContaining("maximum length");
    }
}

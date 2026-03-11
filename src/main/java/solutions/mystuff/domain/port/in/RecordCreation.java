package solutions.mystuff.domain.port.in;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;

public interface RecordCreation {

    void createRecord(UUID orgId, Item item,
            String serviceType,
            ServiceSchedule schedule, Vendor vendor,
            String summary, LocalDate serviceDate,
            String techName);
}

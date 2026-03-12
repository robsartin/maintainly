package solutions.mystuff.domain.service;

import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.port.in.ItemManagement;
import solutions.mystuff.domain.port.out.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

/**
 * Validates and persists new items within a transaction.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Controller->>ItemManagementService: createItem(...)
 *     ItemManagementService->>ItemManagementService: validate fields
 *     ItemManagementService->>ItemRepository: save(item)
 * </div>
 *
 * @see ItemManagement
 * @see ItemRepository
 */
@Service
public class ItemManagementService
        implements ItemManagement {

    private static final Logger log =
            LoggerFactory.getLogger(
                    ItemManagementService.class);
    private static final int MAX_LENGTH = 200;

    private final ItemRepository itemRepo;

    public ItemManagementService(
            ItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    /** Validates input lengths and creates a new item. */
    @Override
    @Transactional
    public Item createItem(
            UUID orgId, String name,
            String location, String manufacturer,
            String modelName, String serialNumber) {
        String trimName = requireNotBlank(name);
        requireMaxLength(trimName, "Item name");
        requireMaxLength(location, "Location");
        requireMaxLength(manufacturer, "Manufacturer");
        requireMaxLength(modelName, "Model name");
        requireMaxLength(serialNumber, "Serial number");

        Item item = new Item();
        item.setOrganizationId(orgId);
        item.setName(trimName);
        setIfPresent(item, location, manufacturer,
                modelName, serialNumber);
        Item saved = itemRepo.save(item);
        log.info("Created item {}", saved.getName());
        return saved;
    }

    private String requireNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Item name is required");
        }
        return value.trim();
    }

    private void requireMaxLength(
            String value, String field) {
        if (value != null
                && value.trim().length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    field + " exceeds maximum length of "
                            + MAX_LENGTH);
        }
    }

    private void setIfPresent(
            Item item, String location,
            String manufacturer, String modelName,
            String serialNumber) {
        if (location != null && !location.isBlank()) {
            item.setLocation(location.trim());
        }
        if (manufacturer != null
                && !manufacturer.isBlank()) {
            item.setManufacturer(manufacturer.trim());
        }
        if (modelName != null && !modelName.isBlank()) {
            item.setModelName(modelName.trim());
        }
        if (serialNumber != null
                && !serialNumber.isBlank()) {
            item.setSerialNumber(serialNumber.trim());
        }
    }
}

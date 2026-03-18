package solutions.mystuff.domain.service;

import java.time.LocalDate;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.Validation;
import solutions.mystuff.domain.port.in.ItemManagement;
import solutions.mystuff.domain.port.out.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;

/**
 * Validates and persists new or updated items within a transaction.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant C as Controller
 *     participant S as ItemManagementService
 *     participant R as ItemRepository
 *     C-&gt;&gt;S: createItem / updateItem
 *     S-&gt;&gt;S: validate fields
 *     S-&gt;&gt;R: save(item)
 *     R--&gt;&gt;S: saved item
 *     S--&gt;&gt;C: Item
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
    private static final int CATEGORY_MAX = 100;

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
            String modelName, String serialNumber,
            String modelNumber, Integer modelYear,
            String category, LocalDate purchaseDate,
            String notes) {
        String trimName = validateFields(name, location,
                manufacturer, modelName, serialNumber,
                modelNumber, category);
        Item item = new Item();
        item.setOrganizationId(orgId);
        item.setName(trimName);
        setAllFields(item, location, manufacturer,
                modelName, serialNumber, modelNumber,
                modelYear, category, purchaseDate, notes);
        Item saved = itemRepo.save(item);
        log.info("Created item {}", saved.getName());
        return saved;
    }

    /** Finds existing item and updates all fields. */
    @Override
    @Transactional
    public Item updateItem(
            UUID orgId, UUID itemId,
            String name, String location,
            String manufacturer, String modelName,
            String serialNumber, String modelNumber,
            Integer modelYear, String category,
            LocalDate purchaseDate, String notes) {
        String trimName = validateFields(name, location,
                manufacturer, modelName, serialNumber,
                modelNumber, category);
        Item item = itemRepo
                .findByIdAndOrganizationId(itemId, orgId)
                .orElseThrow(() -> new NotFoundException(
                        "Item not found"));
        item.setName(trimName);
        setAllFields(item, location, manufacturer,
                modelName, serialNumber, modelNumber,
                modelYear, category, purchaseDate, notes);
        Item saved = itemRepo.save(item);
        log.info("Updated item {}", saved.getName());
        return saved;
    }

    private String validateFields(
            String name, String location,
            String manufacturer, String modelName,
            String serialNumber, String modelNumber,
            String category) {
        String trimName = Validation.requireNotBlank(
                name, "Item name");
        Validation.requireMaxLength(
                trimName, "Item name", MAX_LENGTH);
        Validation.requireMaxLength(
                location, "Location", MAX_LENGTH);
        Validation.requireMaxLength(
                manufacturer, "Manufacturer", MAX_LENGTH);
        Validation.requireMaxLength(
                modelName, "Model name", MAX_LENGTH);
        Validation.requireMaxLength(
                serialNumber, "Serial number", MAX_LENGTH);
        Validation.requireMaxLength(
                modelNumber, "Model number", MAX_LENGTH);
        Validation.requireMaxLength(
                category, "Category", CATEGORY_MAX);
        return trimName;
    }

    private void setAllFields(
            Item item, String location,
            String manufacturer, String modelName,
            String serialNumber, String modelNumber,
            Integer modelYear, String category,
            LocalDate purchaseDate, String notes) {
        item.setLocation(trimOrNull(location));
        item.setManufacturer(trimOrNull(manufacturer));
        item.setModelName(trimOrNull(modelName));
        item.setSerialNumber(trimOrNull(serialNumber));
        item.setModelNumber(trimOrNull(modelNumber));
        item.setCategory(trimOrNull(category));
        item.setModelYear(modelYear);
        item.setPurchaseDate(purchaseDate);
        item.setNotes(trimOrNull(notes));
    }

    private String trimOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

package solutions.mystuff.domain.service;

import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ItemSpec;
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
 * Validates and persists new, updated, or deleted items within a transaction.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant C as Controller
 *     participant S as ItemManagementService
 *     participant R as ItemRepository
 *     C-&gt;&gt;S: createItem / updateItem / deleteItem
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
    public Item createItem(UUID orgId, ItemSpec spec) {
        String trimName = validateFields(spec);
        Item item = new Item();
        item.setOrganizationId(orgId);
        item.setName(trimName);
        setAllFields(item, spec);
        Item saved = itemRepo.save(item);
        log.info("Created item {}", saved.getName());
        return saved;
    }

    /** Finds existing item and updates all fields. */
    @Override
    @Transactional
    public Item updateItem(
            UUID orgId, UUID itemId, ItemSpec spec) {
        String trimName = validateFields(spec);
        Item item = requireItem(orgId, itemId);
        item.setName(trimName);
        setAllFields(item, spec);
        Item saved = itemRepo.save(item);
        log.info("Updated item {}", saved.getName());
        return saved;
    }

    /** Finds and deletes an item by ID. */
    @Override
    @Transactional
    public void deleteItem(UUID orgId, UUID itemId) {
        Item item = requireItem(orgId, itemId);
        itemRepo.deleteByIdAndOrganizationId(
                itemId, orgId);
        log.info("Deleted item {}", item.getName());
    }

    private Item requireItem(UUID orgId, UUID itemId) {
        return itemRepo
                .findByIdAndOrganizationId(itemId, orgId)
                .orElseThrow(() -> new NotFoundException(
                        "Item not found"));
    }

    private String validateFields(ItemSpec spec) {
        String trimName = Validation.requireNotBlank(
                spec.name(), "Item name");
        Validation.requireMaxLength(
                trimName, "Item name", MAX_LENGTH);
        Validation.requireMaxLength(
                spec.location(), "Location", MAX_LENGTH);
        Validation.requireMaxLength(
                spec.manufacturer(),
                "Manufacturer", MAX_LENGTH);
        Validation.requireMaxLength(
                spec.modelName(),
                "Model name", MAX_LENGTH);
        Validation.requireMaxLength(
                spec.serialNumber(),
                "Serial number", MAX_LENGTH);
        Validation.requireMaxLength(
                spec.modelNumber(),
                "Model number", MAX_LENGTH);
        Validation.requireMaxLength(
                spec.category(),
                "Category", CATEGORY_MAX);
        return trimName;
    }

    private void setAllFields(
            Item item, ItemSpec spec) {
        item.setLocation(Validation.trimOrNull(spec.location()));
        item.setManufacturer(
                Validation.trimOrNull(spec.manufacturer()));
        item.setModelName(Validation.trimOrNull(spec.modelName()));
        item.setSerialNumber(
                Validation.trimOrNull(spec.serialNumber()));
        item.setModelNumber(
                Validation.trimOrNull(spec.modelNumber()));
        item.setCategory(Validation.trimOrNull(spec.category()));
        item.setModelYear(spec.modelYear());
        item.setPurchaseDate(spec.purchaseDate());
        item.setNotes(Validation.trimOrNull(spec.notes()));
    }

}

package solutions.mystuff.domain.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ItemSpec;
import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.UuidV7;
import solutions.mystuff.domain.port.out.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("ItemManagementService")
class ItemManagementServiceTest {

    private final ItemRepository itemRepo =
            mock(ItemRepository.class);
    private final ItemManagementService service =
            new ItemManagementService(itemRepo);

    private final UUID orgId = UuidV7.generate();

    @Test
    @DisplayName("should create item with valid name")
    void shouldCreateItem() {
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        ItemSpec spec = new ItemSpec("Furnace",
                null, null, null, null, null,
                null, null, null, null);
        Item item = service.createItem(orgId, spec);
        assertEquals("Furnace", item.getName());
        assertEquals(orgId, item.getOrganizationId());
        verify(itemRepo).save(any(Item.class));
    }

    @Test
    @DisplayName("should trim item name")
    void shouldTrimName() {
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        ItemSpec spec = new ItemSpec("  Furnace  ",
                null, null, null, null, null,
                null, null, null, null);
        Item item = service.createItem(orgId, spec);
        assertEquals("Furnace", item.getName());
    }

    @Test
    @DisplayName("should set optional fields when present")
    void shouldSetOptionalFields() {
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        ItemSpec spec = new ItemSpec("AC", "Roof",
                "Trane", "XR15", "SN-123", "MN-456",
                2024, "HVAC",
                LocalDate.of(2024, 1, 15), "Good unit");
        Item item = service.createItem(orgId, spec);
        assertEquals("Roof", item.getLocation());
        assertEquals("Trane", item.getManufacturer());
        assertEquals("XR15", item.getModelName());
        assertEquals("SN-123", item.getSerialNumber());
        assertEquals("MN-456", item.getModelNumber());
        assertEquals(2024, item.getModelYear());
        assertEquals("HVAC", item.getCategory());
        assertEquals(LocalDate.of(2024, 1, 15),
                item.getPurchaseDate());
        assertEquals("Good unit", item.getNotes());
    }

    @Test
    @DisplayName("should skip blank optional fields")
    void shouldSkipBlankOptional() {
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        ItemSpec spec = new ItemSpec("AC", "  ", "",
                null, null, null, null, null,
                null, null);
        Item item = service.createItem(orgId, spec);
        assertNull(item.getLocation());
        assertNull(item.getManufacturer());
    }

    @Test
    @DisplayName("should reject blank name")
    void shouldRejectBlankName() {
        ItemSpec spec = new ItemSpec("  ", null,
                null, null, null, null, null,
                null, null, null);
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(orgId, spec));
    }

    @Test
    @DisplayName("should reject null name")
    void shouldRejectNullName() {
        ItemSpec spec = new ItemSpec(null, null,
                null, null, null, null, null,
                null, null, null);
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(orgId, spec));
    }

    @Test
    @DisplayName("should reject name exceeding max length")
    void shouldRejectLongName() {
        String longName = "x".repeat(201);
        ItemSpec spec = new ItemSpec(longName, null,
                null, null, null, null, null,
                null, null, null);
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(orgId, spec));
    }

    @Test
    @DisplayName("should reject location exceeding max"
            + " length")
    void shouldRejectLongLocation() {
        String longLoc = "x".repeat(201);
        ItemSpec spec = new ItemSpec("AC", longLoc,
                null, null, null, null, null,
                null, null, null);
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(orgId, spec));
    }

    @Test
    @DisplayName("should update existing item")
    void shouldUpdateItem() {
        UUID itemId = UuidV7.generate();
        Item existing = new Item();
        existing.setOrganizationId(orgId);
        existing.setName("Old Name");
        when(itemRepo.findByIdAndOrganizationId(
                itemId, orgId))
                .thenReturn(Optional.of(existing));
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        ItemSpec spec = new ItemSpec("New Name",
                "Basement", "Trane", "XR15", "SN-999",
                "MN-100", 2025, "HVAC",
                LocalDate.of(2025, 6, 1),
                "Updated notes");
        Item updated = service.updateItem(
                orgId, itemId, spec);
        assertEquals("New Name", updated.getName());
        assertEquals("Basement", updated.getLocation());
        assertEquals("HVAC", updated.getCategory());
        assertEquals("Updated notes", updated.getNotes());
        verify(itemRepo).save(any(Item.class));
    }

    @Test
    @DisplayName("should throw when updating nonexistent"
            + " item")
    void shouldThrowWhenUpdatingNonexistentItem() {
        UUID itemId = UuidV7.generate();
        when(itemRepo.findByIdAndOrganizationId(
                itemId, orgId))
                .thenReturn(Optional.empty());
        ItemSpec spec = new ItemSpec("Name", null,
                null, null, null, null, null,
                null, null, null);
        assertThrows(NotFoundException.class,
                () -> service.updateItem(
                        orgId, itemId, spec));
    }

    @Test
    @DisplayName("should clear fields on update when"
            + " blank")
    void shouldClearFieldsWhenBlank() {
        UUID itemId = UuidV7.generate();
        Item existing = new Item();
        existing.setOrganizationId(orgId);
        existing.setName("AC");
        existing.setLocation("Roof");
        existing.setCategory("HVAC");
        when(itemRepo.findByIdAndOrganizationId(
                itemId, orgId))
                .thenReturn(Optional.of(existing));
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        ItemSpec spec = new ItemSpec("AC", "", null,
                null, null, null, null, null,
                null, null);
        Item updated = service.updateItem(
                orgId, itemId, spec);
        assertNull(updated.getLocation());
        assertNull(updated.getCategory());
    }
}

package solutions.mystuff.domain.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
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
        Item item = service.createItem(
                orgId, "Furnace", null, null,
                null, null, null, null,
                null, null, null);
        assertEquals("Furnace", item.getName());
        assertEquals(orgId, item.getOrganizationId());
        verify(itemRepo).save(any(Item.class));
    }

    @Test
    @DisplayName("should trim item name")
    void shouldTrimName() {
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        Item item = service.createItem(
                orgId, "  Furnace  ", null,
                null, null, null, null,
                null, null, null, null);
        assertEquals("Furnace", item.getName());
    }

    @Test
    @DisplayName("should set optional fields when present")
    void shouldSetOptionalFields() {
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        Item item = service.createItem(orgId, "AC",
                "Roof", "Trane", "XR15", "SN-123",
                "MN-456", 2024, "HVAC",
                LocalDate.of(2024, 1, 15), "Good unit");
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
        Item item = service.createItem(
                orgId, "AC", "  ", "", null,
                null, null, null, null, null, null);
        assertNull(item.getLocation());
        assertNull(item.getManufacturer());
    }

    @Test
    @DisplayName("should reject blank name")
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(
                        orgId, "  ", null,
                        null, null, null, null,
                        null, null, null, null));
    }

    @Test
    @DisplayName("should reject null name")
    void shouldRejectNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(
                        orgId, null, null,
                        null, null, null, null,
                        null, null, null, null));
    }

    @Test
    @DisplayName("should reject name exceeding max length")
    void shouldRejectLongName() {
        String longName = "x".repeat(201);
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(
                        orgId, longName, null,
                        null, null, null, null,
                        null, null, null, null));
    }

    @Test
    @DisplayName("should reject location exceeding max"
            + " length")
    void shouldRejectLongLocation() {
        String longLoc = "x".repeat(201);
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(
                        orgId, "AC", longLoc,
                        null, null, null, null,
                        null, null, null, null));
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
        Item updated = service.updateItem(
                orgId, itemId, "New Name", "Basement",
                "Trane", "XR15", "MN-100", 2025,
                "SN-999", LocalDate.of(2025, 6, 1),
                "HVAC", "Updated notes");
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
        assertThrows(NotFoundException.class,
                () -> service.updateItem(
                        orgId, itemId, "Name", null,
                        null, null, null, null,
                        null, null, null, null));
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
        Item updated = service.updateItem(
                orgId, itemId, "AC", "", null,
                null, null, null, null,
                null, null, null);
        assertNull(updated.getLocation());
        assertNull(updated.getCategory());
    }
}

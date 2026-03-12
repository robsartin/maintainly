package solutions.mystuff.domain.service;

import java.util.UUID;

import solutions.mystuff.domain.model.Item;
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
                orgId, "Furnace", null, null, null, null);
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
                null, null, null);
        assertEquals("Furnace", item.getName());
    }

    @Test
    @DisplayName("should set optional fields when present")
    void shouldSetOptionalFields() {
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        Item item = service.createItem(orgId, "AC",
                "Roof", "Trane", "XR15", "SN-123");
        assertEquals("Roof", item.getLocation());
        assertEquals("Trane", item.getManufacturer());
        assertEquals("XR15", item.getModelName());
        assertEquals("SN-123", item.getSerialNumber());
    }

    @Test
    @DisplayName("should skip blank optional fields")
    void shouldSkipBlankOptional() {
        when(itemRepo.save(any(Item.class)))
                .thenAnswer(i -> i.getArgument(0));
        Item item = service.createItem(
                orgId, "AC", "  ", "", null, null);
        assertNull(item.getLocation());
        assertNull(item.getManufacturer());
    }

    @Test
    @DisplayName("should reject blank name")
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(
                        orgId, "  ", null,
                        null, null, null));
    }

    @Test
    @DisplayName("should reject null name")
    void shouldRejectNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(
                        orgId, null, null,
                        null, null, null));
    }

    @Test
    @DisplayName("should reject name exceeding max length")
    void shouldRejectLongName() {
        String longName = "x".repeat(201);
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(
                        orgId, longName, null,
                        null, null, null));
    }

    @Test
    @DisplayName("should reject location exceeding max length")
    void shouldRejectLongLocation() {
        String longLoc = "x".repeat(201);
        assertThrows(IllegalArgumentException.class,
                () -> service.createItem(
                        orgId, "AC", longLoc,
                        null, null, null));
    }
}

package solutions.mystuff.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("VendorAltPhone")
class VendorAltPhoneTest {

    @Test
    @DisplayName("should have null defaults")
    void shouldHaveNullDefaults() {
        VendorAltPhone p = new VendorAltPhone();
        assertNull(p.getOrganizationId());
        assertNull(p.getVendor());
        assertNull(p.getPhone());
        assertNull(p.getLabel());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetFields() {
        VendorAltPhone p = new VendorAltPhone();
        var orgId = UuidV7.generate();
        Vendor vendor = new Vendor();
        p.setOrganizationId(orgId);
        p.setVendor(vendor);
        p.setPhone("555-0200");
        p.setLabel("After hours");
        assertEquals(orgId, p.getOrganizationId());
        assertEquals(vendor, p.getVendor());
        assertEquals("555-0200", p.getPhone());
        assertEquals("After hours", p.getLabel());
    }
}

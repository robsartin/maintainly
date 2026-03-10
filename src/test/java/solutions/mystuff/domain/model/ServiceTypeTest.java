package solutions.mystuff.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("ServiceType")
class ServiceTypeTest {

    @Test
    @DisplayName("should have null defaults")
    void shouldHaveNullDefaults() {
        ServiceType st = new ServiceType();
        assertNull(st.getOrganizationId());
        assertNull(st.getCode());
        assertNull(st.getName());
        assertNull(st.getDescription());
    }

    @Test
    @DisplayName("should set and get all fields")
    void shouldSetAndGetFields() {
        ServiceType st = new ServiceType();
        var orgId = UuidV7.generate();
        st.setOrganizationId(orgId);
        st.setCode("HVAC_INSPECT");
        st.setName("HVAC Inspection");
        st.setDescription("Annual HVAC check");
        assertEquals(orgId, st.getOrganizationId());
        assertEquals("HVAC_INSPECT", st.getCode());
        assertEquals("HVAC Inspection", st.getName());
        assertEquals("Annual HVAC check",
                st.getDescription());
    }
}

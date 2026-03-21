package solutions.mystuff.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AuditAction")
class AuditActionTest {

    @Test
    @DisplayName("should have five values")
    void shouldHaveFiveValues() {
        assertEquals(5, AuditAction.values().length);
    }

    @Test
    @DisplayName("should parse from string")
    void shouldParseFromString() {
        assertEquals(AuditAction.CREATE,
                AuditAction.valueOf("CREATE"));
        assertEquals(AuditAction.UPDATE,
                AuditAction.valueOf("UPDATE"));
        assertEquals(AuditAction.DELETE,
                AuditAction.valueOf("DELETE"));
        assertEquals(AuditAction.COMPLETE,
                AuditAction.valueOf("COMPLETE"));
        assertEquals(AuditAction.SKIP,
                AuditAction.valueOf("SKIP"));
    }
}

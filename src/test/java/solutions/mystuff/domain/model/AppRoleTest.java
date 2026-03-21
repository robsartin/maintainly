package solutions.mystuff.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AppRole")
class AppRoleTest {

    @Test
    @DisplayName("should have four roles")
    void shouldHaveFourRoles() {
        assertThat(AppRole.values()).hasSize(4);
    }

    @Test
    @DisplayName("should order ADMIN first")
    void shouldOrderAdminFirst() {
        assertThat(AppRole.ADMIN.ordinal())
                .isLessThan(AppRole.VIEWER.ordinal());
    }

    @Test
    @DisplayName("should parse from string")
    void shouldParseFromString() {
        assertThat(AppRole.valueOf("TECHNICIAN"))
                .isEqualTo(AppRole.TECHNICIAN);
    }

    @Test
    @DisplayName("should have correct privilege order")
    void shouldHaveCorrectPrivilegeOrder() {
        assertThat(AppRole.ADMIN.ordinal())
                .isLessThan(
                        AppRole.FACILITY_MANAGER
                                .ordinal());
        assertThat(AppRole.FACILITY_MANAGER.ordinal())
                .isLessThan(
                        AppRole.TECHNICIAN.ordinal());
        assertThat(AppRole.TECHNICIAN.ordinal())
                .isLessThan(AppRole.VIEWER.ordinal());
    }
}

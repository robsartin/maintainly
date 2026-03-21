package solutions.mystuff.application.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure
        .AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet
        .request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.view;

/**
 * Integration tests for {@link DashboardController}.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Test->>MockMvc: GET /
 *     MockMvc->>DashboardController: dashboard(...)
 *     DashboardController-->>MockMvc: dashboard view
 * </div>
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dashboard Controller Integration")
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should render dashboard for user with org")
    void shouldRenderDashboardWhenUserHasOrg()
            throws Exception {
        mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists(
                        "overdueCount"))
                .andExpect(model().attributeExists(
                        "dueSoonCount"))
                .andExpect(model().attributeExists(
                        "totalItems"))
                .andExpect(model().attributeExists(
                        "recentRecords"))
                .andExpect(model().attributeExists(
                        "username"))
                .andExpect(model().attributeExists(
                        "organization"))
                .andExpect(model().attributeExists(
                        "facilities"))
                .andExpect(model().attributeExists(
                        "facilitySummaries"));
    }

    @Test
    @DisplayName("should show no-org for unknown user")
    void shouldShowNoOrgWhenUserLacksOrganization()
            throws Exception {
        mockMvc.perform(get("/")
                        .with(user("unknown")
                                .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute(
                        "noOrganization", true));
    }

    @Test
    @DisplayName("should render org-wide totals when no facility selected")
    void shouldRenderOrgWideTotalsWhenNoFacilitySelected()
            throws Exception {
        mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists(
                        "overdueCount"))
                .andExpect(model().attributeExists(
                        "totalItems"))
                .andExpect(model().attributeDoesNotExist(
                        "selectedFacilityName"));
    }

    @Test
    @DisplayName("should accept facilityId query parameter")
    void shouldAcceptFacilityIdWhenQueryParamProvided()
            throws Exception {
        mockMvc.perform(get("/")
                        .param("facilityId",
                                "00000000-0000-0000-0000-"
                                + "000000000001")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists(
                        "overdueCount"));
    }

}

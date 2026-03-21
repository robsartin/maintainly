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
 * Integration tests for {@link ActivityController}.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Test-&gt;&gt;MockMvc: GET /activity
 *     MockMvc-&gt;&gt;ActivityController: activity(...)
 *     ActivityController--&gt;&gt;MockMvc: activity view
 * </div>
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Activity Controller Integration")
class ActivityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should render activity page for user"
            + " with org")
    void shouldRenderActivityWhenUserHasOrg()
            throws Exception {
        mockMvc.perform(get("/activity")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("activity"))
                .andExpect(model().attributeExists(
                        "auditEntries"))
                .andExpect(model().attributeExists(
                        "username"))
                .andExpect(model().attributeExists(
                        "organization"));
    }

    @Test
    @DisplayName("should show no-org for unknown user")
    void shouldShowNoOrgWhenUserLacksOrganization()
            throws Exception {
        mockMvc.perform(get("/activity")
                        .with(user("unknown")
                                .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("activity"))
                .andExpect(model().attribute(
                        "noOrganization", true));
    }
}

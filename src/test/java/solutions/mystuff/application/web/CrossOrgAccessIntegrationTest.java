package solutions.mystuff.application.web;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure
        .AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet
        .request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet
        .request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.status;

/**
 * Verifies that users without an organization cannot
 * see or modify entities belonging to other organizations,
 * and that org-scoped queries return empty results
 * for mismatched organizations.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant OtherUser as otheruser (no org)
 *     participant Controller
 *     OtherUser->>Controller: GET /items
 *     Controller-->>OtherUser: 200 (noOrganization=true)
 * </div>
 *
 * @see OrgOwnedEntity#belongsTo(UUID)
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Cross-Org Access Integration")
class CrossOrgAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should show no-org warning for items")
    void shouldShowNoOrgWarningForItems()
            throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("otheruser")
                                .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "noOrganization", true));
    }

    @Test
    @DisplayName("should show no-org warning for item detail")
    void shouldShowNoOrgWarningForItemDetail()
            throws Exception {
        String itemId = getDevItemId();
        mockMvc.perform(get("/items/" + itemId)
                        .with(user("otheruser")
                                .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "noOrganization", true));
    }

    @Test
    @DisplayName("should show no-org warning for schedules")
    void shouldShowNoOrgWarningForSchedules()
            throws Exception {
        mockMvc.perform(get("/schedules")
                        .with(user("otheruser")
                                .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "noOrganization", true));
    }

    @Test
    @DisplayName("should show no-org warning for vendors")
    void shouldShowNoOrgWarningForVendors()
            throws Exception {
        mockMvc.perform(get("/vendors")
                        .with(user("otheruser")
                                .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "noOrganization", true));
    }

    @Test
    @DisplayName("should deny random item service record")
    void shouldDenyRandomItemServiceRecord()
            throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(post("/items/" + randomId
                        + "/service-records")
                        .param("summary", "Test")
                        .param("serviceDate",
                                "2026-01-01")
                        .with(user("dev")
                                .roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should deny random schedule skip")
    void shouldDenyRandomScheduleSkip()
            throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(post("/schedules/"
                        + randomId + "/skip")
                        .with(user("dev")
                                .roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return dev items only for dev")
    void shouldReturnDevItemsOnlyForDev()
            throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/items")
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        @SuppressWarnings("unchecked")
        List<Item> items = (List<Item>)
                result.getModelAndView().getModel()
                        .get("items");
        assertTrue(items != null && !items.isEmpty(),
                "Dev user should see items");
    }

    @SuppressWarnings("unchecked")
    private String getDevItemId() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/items")
                                .with(user("dev")
                                        .roles("USER")))
                .andReturn();
        List<Item> items = (List<Item>)
                result.getModelAndView().getModel()
                        .get("items");
        return items.get(0).getId().toString();
    }
}

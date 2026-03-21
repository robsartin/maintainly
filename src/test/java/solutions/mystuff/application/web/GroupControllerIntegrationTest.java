package solutions.mystuff.application.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure
        .AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web
        .servlet.request
        .SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web
        .servlet.request
        .SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions
        .assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Group Controller Integration")
class GroupControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should show groups page")
    void shouldShowGroupsPage() throws Exception {
        mockMvc.perform(get("/settings/groups")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model()
                        .attributeExists("groups"));
    }

    @Test
    @DisplayName("should create group")
    void shouldCreateGroup() throws Exception {
        mockMvc.perform(post("/settings/groups")
                        .param("name", "Test Admins")
                        .param("role", "ADMIN")
                        .param("description",
                                "Admin group")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/settings/groups"));
    }

    @Test
    @DisplayName("should list created group")
    void shouldListCreatedGroup() throws Exception {
        mockMvc.perform(post("/settings/groups")
                .param("name", "Listed Group")
                .param("role", "VIEWER")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        MvcResult result = mockMvc.perform(
                        get("/settings/groups")
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(html.contains("Listed Group"),
                "group should appear on page");
    }

    @Test
    @DisplayName("should update group")
    void shouldUpdateGroup() throws Exception {
        mockMvc.perform(post("/settings/groups")
                .param("name", "UpdateMe")
                .param("role", "TECHNICIAN")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        String groupId = findGroupId("UpdateMe");

        mockMvc.perform(put(
                        "/settings/groups/" + groupId)
                        .param("name", "Updated Group")
                        .param("role", "ADMIN")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/settings/groups"));
    }

    @Test
    @DisplayName("should delete group")
    void shouldDeleteGroup() throws Exception {
        mockMvc.perform(post("/settings/groups")
                .param("name", "DeleteMe")
                .param("role", "VIEWER")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        String groupId = findGroupId("DeleteMe");

        mockMvc.perform(delete(
                        "/settings/groups/" + groupId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/settings/groups"));
    }

    @Test
    @DisplayName("should reject blank group name")
    void shouldRejectBlankGroupName() throws Exception {
        mockMvc.perform(post("/settings/groups")
                        .param("name", "  ")
                        .param("role", "ADMIN")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/settings/groups"));
    }

    private String findGroupId(String name)
            throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/settings/groups")
                                .with(user("dev")
                                        .roles("USER")))
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        int nameIdx = html.indexOf(name);
        assertTrue(nameIdx > 0,
                "Group not found: " + name);
        String marker =
                "name=\"_method\" value=\"PUT\"";
        int putIdx = html.indexOf(marker, nameIdx);
        if (putIdx < 0) {
            putIdx = html.lastIndexOf(
                    marker, nameIdx);
        }
        String actionMarker = "/settings/groups/";
        int actionIdx = html.lastIndexOf(
                actionMarker, putIdx);
        int idStart =
                actionIdx + actionMarker.length();
        int idEnd = html.indexOf("\"", idStart);
        return html.substring(idStart, idEnd);
    }
}

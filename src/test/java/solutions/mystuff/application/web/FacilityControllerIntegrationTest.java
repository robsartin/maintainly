package solutions.mystuff.application.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation
        .Autowired;
import org.springframework.boot.test.context
        .SpringBootTest;
import org.springframework.boot.webmvc.test
        .autoconfigure.AutoConfigureMockMvc;
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
@DisplayName("Facility Controller Integration")
class FacilityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should show facilities page")
    void shouldShowFacilitiesPage()
            throws Exception {
        mockMvc.perform(get("/facilities")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model()
                        .attributeExists("facilities"));
    }

    @Test
    @DisplayName("should add facility")
    void shouldAddFacility() throws Exception {
        mockMvc.perform(post("/facilities")
                        .param("name",
                                "Integration Office")
                        .param("addressLine1",
                                "100 Main St")
                        .param("city", "TestCity")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status()
                        .is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/facilities"));
    }

    @Test
    @DisplayName("should reject blank name on add")
    void shouldRejectBlankName() throws Exception {
        mockMvc.perform(post("/facilities")
                        .param("name", "  ")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status()
                        .is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/facilities"));
    }

    @Test
    @DisplayName("should edit facility")
    void shouldEditFacility() throws Exception {
        mockMvc.perform(post("/facilities")
                .param("name", "EditMe Office")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        String facilityId =
                findFacilityId("EditMe Office");

        mockMvc.perform(
                        put("/facilities/" + facilityId)
                                .param("name",
                                        "Edited Office")
                                .param("city",
                                        "NewCity")
                                .with(user("dev")
                                        .roles("USER"))
                                .with(csrf()))
                .andExpect(status()
                        .is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/facilities"));
    }

    @Test
    @DisplayName("should delete facility")
    void shouldDeleteFacility() throws Exception {
        mockMvc.perform(post("/facilities")
                .param("name", "DeleteMe Office")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        String facilityId =
                findFacilityId("DeleteMe Office");

        mockMvc.perform(
                        delete("/facilities/"
                                + facilityId)
                                .with(user("dev")
                                        .roles("USER"))
                                .with(csrf()))
                .andExpect(status()
                        .is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/facilities"));
    }

    @Test
    @DisplayName("should render edit toggle for facility")
    void shouldRenderEditToggle() throws Exception {
        mockMvc.perform(post("/facilities")
                .param("name", "ToggleTest Office")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        MvcResult result = mockMvc.perform(
                        get("/facilities")
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(
                html.contains(
                        "data-toggle-form=\"edit-"),
                "should have data-toggle-form"
                        + " for edit");
    }

    @Test
    @DisplayName("should show facility with address")
    void shouldShowFacilityWithAddress()
            throws Exception {
        mockMvc.perform(post("/facilities")
                .param("name", "Address Office")
                .param("addressLine1", "789 Oak Ave")
                .param("city", "Portland")
                .param("stateProvince", "OR")
                .param("postalCode", "97201")
                .param("country", "US")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        MvcResult result = mockMvc.perform(
                        get("/facilities")
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(
                html.contains("Address Office"),
                "facility name should appear");
        assertTrue(
                html.contains("Portland"),
                "city should appear");
    }

    private String findFacilityId(String name)
            throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/facilities")
                                .with(user("dev")
                                        .roles("USER")))
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        int nameIdx = html.indexOf(name);
        assertTrue(nameIdx > 0,
                "Facility not found: " + name);
        String marker =
                "name=\"_method\" value=\"PUT\"";
        int putIdx = html.indexOf(marker, nameIdx);
        if (putIdx < 0) {
            putIdx = html.lastIndexOf(
                    marker, nameIdx);
        }
        String actionMarker = "/facilities/";
        int actionIdx = html.lastIndexOf(
                actionMarker, putIdx);
        int idStart =
                actionIdx + actionMarker.length();
        int idEnd = html.indexOf("\"", idStart);
        return html.substring(idStart, idEnd);
    }
}

package com.robsartin.maintainly.application.web;

import java.util.List;

import com.robsartin.maintainly.domain.model.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Property Controller Integration")
class PropertyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should render property list for authenticated user")
    void shouldRenderPropertyList() throws Exception {
        mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("properties"));
    }

    @Test
    @DisplayName("should search properties")
    void shouldSearchProperties() throws Exception {
        mockMvc.perform(get("/")
                        .param("q", "Main")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("properties"));
    }

    @Test
    @DisplayName("should add service request")
    void shouldAddServiceRequest() throws Exception {
        String propId = getFirstPropertyId();
        mockMvc.perform(post("/properties/service")
                        .param("propertyId", propId)
                        .param("description", "Fix HVAC")
                        .param("serviceDate", "2026-04-15")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should complete service request")
    void shouldCompleteServiceRequest() throws Exception {
        mockMvc.perform(post("/properties/service/complete")
                        .param("serviceRequestId",
                                "00000000-0000-0000-0000-000000000000")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should handle invalid date format")
    void shouldHandleInvalidDate() throws Exception {
        String propId = getFirstPropertyId();
        mockMvc.perform(post("/properties/service")
                        .param("propertyId", propId)
                        .param("description", "Fix HVAC")
                        .param("serviceDate", "not-a-date")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should show no-org message for new user")
    void shouldShowNoOrgForNewUser() throws Exception {
        mockMvc.perform(get("/")
                        .with(user("unknown").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "noOrganization", true));
    }

    @SuppressWarnings("unchecked")
    private String getFirstPropertyId() throws Exception {
        MvcResult result = mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andReturn();
        List<Property> props = (List<Property>)
                result.getModelAndView().getModel()
                        .get("properties");
        return props.get(0).getId().toString();
    }
}

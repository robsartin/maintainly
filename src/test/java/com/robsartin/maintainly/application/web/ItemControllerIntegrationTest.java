package com.robsartin.maintainly.application.web;

import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.Item;
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
@DisplayName("Item Controller Integration")
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should render item list for authenticated user")
    void shouldRenderItemList() throws Exception {
        mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists(
                        "schedules"))
                .andExpect(model().attributeExists(
                        "username"));
    }

    @Test
    @DisplayName("should search items")
    void shouldSearchItems() throws Exception {
        mockMvc.perform(get("/")
                        .param("q", "Furnace")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("q", "Furnace"));
    }

    @Test
    @DisplayName("should return all items with blank search")
    void shouldReturnAllWithBlankSearch() throws Exception {
        mockMvc.perform(get("/")
                        .param("q", "  ")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(
                        model().attributeDoesNotExist("q"));
    }

    @Test
    @DisplayName("should add service record")
    void shouldAddServiceRecord() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/service/record")
                        .param("itemId", itemId)
                        .param("summary", "Filter replaced")
                        .param("serviceDate", "2026-04-15")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"));
    }

    @Test
    @DisplayName("should handle invalid date format")
    void shouldHandleInvalidDate() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/service/record")
                        .param("itemId", itemId)
                        .param("summary", "Test")
                        .param("serviceDate", "not-a-date")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should handle invalid item ID")
    void shouldHandleInvalidItemId() throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/service/record")
                        .param("itemId", fakeId.toString())
                        .param("summary", "Test")
                        .param("serviceDate", "2026-04-15")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should show no-org for new user")
    void shouldShowNoOrgForNewUser() throws Exception {
        mockMvc.perform(get("/")
                        .with(user("unknown").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "noOrganization", true));
    }

    @SuppressWarnings("unchecked")
    private String getFirstItemId() throws Exception {
        MvcResult result = mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andReturn();
        List<Item> items = (List<Item>)
                result.getModelAndView().getModel()
                        .get("items");
        return items.get(0).getId().toString();
    }
}

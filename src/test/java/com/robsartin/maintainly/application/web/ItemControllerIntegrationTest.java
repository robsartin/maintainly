package com.robsartin.maintainly.application.web;

import java.util.List;
import java.util.UUID;

import com.robsartin.maintainly.domain.model.Item;
import com.robsartin.maintainly.domain.model.ServiceSchedule;
import com.robsartin.maintainly.domain.model.ServiceType;
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
                        "username"))
                .andExpect(model().attributeExists(
                        "organization"));
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
    @DisplayName("should log service for item")
    void shouldLogItemService() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/item/log")
                        .param("itemId", itemId)
                        .param("summary", "Filter replaced")
                        .param("serviceDate", "2026-04-15")
                        .param("techName", "Jane")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"));
    }

    @Test
    @DisplayName("should log item service without tech name")
    void shouldLogItemServiceNoTech() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/item/log")
                        .param("itemId", itemId)
                        .param("summary", "Quick check")
                        .param("serviceDate", "2026-04-16")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"));
    }

    @Test
    @DisplayName("should handle invalid date on item log")
    void shouldHandleInvalidDate() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/item/log")
                        .param("itemId", itemId)
                        .param("summary", "Test")
                        .param("serviceDate", "not-a-date")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should handle invalid item ID on log")
    void shouldHandleInvalidItemId() throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/item/log")
                        .param("itemId", fakeId.toString())
                        .param("summary", "Test")
                        .param("serviceDate", "2026-04-15")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should handle invalid schedule ID on log")
    void shouldHandleInvalidScheduleId() throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/schedule/log")
                        .param("scheduleId",
                                fakeId.toString())
                        .param("summary", "Test")
                        .param("serviceDate", "2026-04-15")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should provide today and soon dates")
    void shouldProvideScheduleDates() throws Exception {
        mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(
                        model().attributeExists("today"))
                .andExpect(
                        model().attributeExists("soon"));
    }

    @Test
    @DisplayName("should log service for a schedule")
    void shouldLogServiceForSchedule() throws Exception {
        String scheduleId = getFirstScheduleId();
        mockMvc.perform(post("/schedule/log")
                        .param("scheduleId", scheduleId)
                        .param("summary", "Routine check")
                        .param("serviceDate", "2026-03-10")
                        .param("techName", "John")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"));
    }

    @Test
    @DisplayName("should delete a schedule")
    void shouldDeleteSchedule() throws Exception {
        String scheduleId = getFirstScheduleId();
        mockMvc.perform(post("/schedule/delete")
                        .param("scheduleId", scheduleId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"));
    }

    @Test
    @DisplayName("should handle invalid schedule on delete")
    void shouldHandleInvalidScheduleDelete()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/schedule/delete")
                        .param("scheduleId",
                                fakeId.toString())
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should schedule service from item")
    void shouldScheduleFromItem() throws Exception {
        String itemId = getFirstItemId();
        String svcTypeId = getFirstServiceTypeId();
        mockMvc.perform(post("/item/schedule")
                        .param("itemId", itemId)
                        .param("serviceTypeId", svcTypeId)
                        .param("nextDueDate", "2026-09-15")
                        .param("frequencyInterval", "6")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"));
    }

    @Test
    @DisplayName("should handle invalid item on schedule")
    void shouldHandleInvalidItemSchedule()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        String svcTypeId = getFirstServiceTypeId();
        mockMvc.perform(post("/item/schedule")
                        .param("itemId",
                                fakeId.toString())
                        .param("serviceTypeId", svcTypeId)
                        .param("nextDueDate", "2026-09-15")
                        .param("frequencyInterval", "6")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should provide service types and units")
    void shouldProvideServiceTypesAndUnits()
            throws Exception {
        mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "serviceTypes"))
                .andExpect(model().attributeExists(
                        "frequencyUnits"));
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
        return ((List<Item>) getModel("items"))
                .get(0).getId().toString();
    }

    @SuppressWarnings("unchecked")
    private String getFirstScheduleId()
            throws Exception {
        return ((List<ServiceSchedule>)
                getModel("schedules"))
                .get(0).getId().toString();
    }

    @SuppressWarnings("unchecked")
    private String getFirstServiceTypeId()
            throws Exception {
        return ((List<ServiceType>)
                getModel("serviceTypes"))
                .get(0).getId().toString();
    }

    private Object getModel(String attr)
            throws Exception {
        MvcResult result = mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andReturn();
        return result.getModelAndView().getModel()
                .get(attr);
    }
}

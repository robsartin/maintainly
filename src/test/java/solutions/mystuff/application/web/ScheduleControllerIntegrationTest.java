package solutions.mystuff.application.web;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.ServiceSchedule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Schedule Controller Integration")
class ScheduleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should render paginated schedule list")
    void shouldRenderScheduleList() throws Exception {
        mockMvc.perform(get("/schedules")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "schedules"))
                .andExpect(model().attributeExists(
                        "schedPage"))
                .andExpect(model().attributeExists(
                        "today"))
                .andExpect(model().attributeExists(
                        "soon"))
                .andExpect(model().attributeExists(
                        "username"))
                .andExpect(model().attributeExists(
                        "organization"));
    }

    @Test
    @DisplayName("should limit schedules to page size")
    void shouldLimitSchedulesToPageSize()
            throws Exception {
        mockMvc.perform(get("/schedules")
                        .param("size", "3")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("schedules",
                        hasSize(lessThanOrEqualTo(3))));
    }

    @Test
    @DisplayName("should include Link header for schedules")
    void shouldIncludeLinkHeader() throws Exception {
        mockMvc.perform(get("/schedules")
                        .param("size", "2")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(header().string("Link",
                        containsString("rel=\"first\"")))
                .andExpect(header().string("Link",
                        containsString("rel=\"last\"")));
    }

    @Test
    @DisplayName("should log service for a schedule")
    void shouldLogServiceForSchedule() throws Exception {
        String scheduleId = getFirstScheduleId();
        mockMvc.perform(post("/schedules/log")
                        .param("scheduleId", scheduleId)
                        .param("summary", "Routine check")
                        .param("serviceDate", "2026-03-10")
                        .param("techName", "John")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules"));
    }

    @Test
    @DisplayName("should handle invalid schedule on log")
    void shouldHandleInvalidScheduleId()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/schedules/log")
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
    @DisplayName("should delete a schedule")
    void shouldDeleteSchedule() throws Exception {
        String scheduleId = getFirstScheduleId();
        mockMvc.perform(post("/schedules/delete")
                        .param("scheduleId", scheduleId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules"));
    }

    @Test
    @DisplayName("should handle invalid schedule delete")
    void shouldHandleInvalidScheduleDelete()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/schedules/delete")
                        .param("scheduleId",
                                fakeId.toString())
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should render nav icons in schedules page")
    void shouldRenderNavIcons() throws Exception {
        mockMvc.perform(get("/schedules")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("class=\"nav-link\"")))
                .andExpect(content().string(
                        containsString("<svg")));
    }

    @Test
    @DisplayName("should provide schedule form data")
    void shouldProvideScheduleFormData()
            throws Exception {
        mockMvc.perform(get("/schedules")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "frequencyUnits"));
    }

    @Test
    @DisplayName("should create schedule from schedules page")
    void shouldCreateScheduleFromSchedules()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/schedules/create")
                        .param("itemId", itemId)
                        .param("serviceType",
                                "HVAC Inspection")
                        .param("nextDueDate", "2026-12-01")
                        .param("frequencyInterval", "6")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules"));
    }

    @Test
    @DisplayName("should edit a schedule")
    void shouldEditSchedule() throws Exception {
        String scheduleId = getFirstScheduleId();
        mockMvc.perform(post("/schedules/edit")
                        .param("scheduleId", scheduleId)
                        .param("serviceType",
                                "Updated Type")
                        .param("nextDueDate", "2026-12-15")
                        .param("frequencyInterval", "3")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules"));
    }

    @Test
    @DisplayName("should handle invalid schedule on edit")
    void shouldHandleInvalidScheduleEdit()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/schedules/edit")
                        .param("scheduleId",
                                fakeId.toString())
                        .param("serviceType", "Test")
                        .param("nextDueDate", "2026-12-15")
                        .param("frequencyInterval", "1")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should show no-org for new user")
    void shouldShowNoOrgForNewUser() throws Exception {
        mockMvc.perform(get("/schedules")
                        .with(user("unknown").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "noOrganization", true));
    }

    @Test
    @DisplayName("should reject blank service type"
            + " on edit")
    void shouldRejectBlankServiceTypeOnEdit()
            throws Exception {
        String scheduleId = getFirstScheduleId();
        mockMvc.perform(post("/schedules/edit")
                        .param("scheduleId", scheduleId)
                        .param("serviceType", "  ")
                        .param("nextDueDate", "2026-12-15")
                        .param("frequencyInterval", "1")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject zero interval on edit")
    void shouldRejectZeroIntervalOnEdit()
            throws Exception {
        String scheduleId = getFirstScheduleId();
        mockMvc.perform(post("/schedules/edit")
                        .param("scheduleId", scheduleId)
                        .param("serviceType", "Test")
                        .param("nextDueDate", "2026-12-15")
                        .param("frequencyInterval", "0")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject invalid date on create")
    void shouldRejectInvalidDateOnCreate()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/schedules/create")
                        .param("itemId", itemId)
                        .param("serviceType", "Test")
                        .param("nextDueDate", "bad-date")
                        .param("frequencyInterval", "1")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @SuppressWarnings("unchecked")
    private String getFirstScheduleId()
            throws Exception {
        return ((List<ServiceSchedule>)
                getScheduleModel("schedules"))
                .get(0).getId().toString();
    }

    private String getFirstItemId() throws Exception {
        List<ServiceSchedule> schedules =
                getScheduleList();
        return schedules.get(0).getItem().getId()
                .toString();
    }

    @SuppressWarnings("unchecked")
    private List<ServiceSchedule> getScheduleList()
            throws Exception {
        return (List<ServiceSchedule>)
                getScheduleModel("schedules");
    }

    private Object getScheduleModel(String attr)
            throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/schedules")
                                .with(user("dev")
                                        .roles("USER")))
                .andReturn();
        return result.getModelAndView().getModel()
                .get(attr);
    }
}

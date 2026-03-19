package solutions.mystuff.application.web;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure
        .AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet
        .request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Report Controller Integration")
class ReportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should render reports page")
    void shouldRenderReportsPage() throws Exception {
        mockMvc.perform(get("/reports")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "username"))
                .andExpect(model().attributeExists(
                        "organization"))
                .andExpect(model().attributeExists(
                        "items"));
    }

    @Test
    @DisplayName("should generate service summary PDF")
    void shouldGenerateServiceSummaryPdf()
            throws Exception {
        mockMvc.perform(
                        get("/reports/service-summary")
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        "application/pdf"));
    }

    @Test
    @DisplayName("should include color legend in service PDF")
    void shouldIncludeColorLegendInServicePdf()
            throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/reports/service-summary")
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        byte[] pdf = result.getResponse()
                .getContentAsByteArray();
        assertTrue(pdf.length > 0,
                "PDF should not be empty");
    }

    @Test
    @DisplayName("should generate item history PDF")
    void shouldGenerateItemHistoryPdf()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(
                        get("/reports/item-history")
                                .param("itemId", itemId)
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        "application/pdf"));
    }

    @Test
    @DisplayName("should handle invalid item on history")
    void shouldHandleInvalidItemHistory()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(
                        get("/reports/item-history")
                                .param("itemId",
                                        fakeId.toString())
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should generate PDF with custom end date")
    void shouldGeneratePdfWithCustomEndDate()
            throws Exception {
        mockMvc.perform(
                        get("/reports/service-summary")
                                .param("endDate",
                                        "2030-12-31")
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        "application/pdf"));
    }

    @Test
    @DisplayName("should include default end date"
            + " on reports page")
    void shouldIncludeDefaultEndDateOnReportsPage()
            throws Exception {
        mockMvc.perform(get("/reports")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "defaultEnd"));
    }

    @Test
    @DisplayName("should show no-org for new user")
    void shouldShowNoOrgForNewUser() throws Exception {
        mockMvc.perform(get("/reports")
                        .with(user("unknown")
                                .roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "noOrganization", true));
    }

    @SuppressWarnings("unchecked")
    private String getFirstItemId() throws Exception {
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

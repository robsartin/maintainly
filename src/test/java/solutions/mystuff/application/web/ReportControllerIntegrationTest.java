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
                        "organization"));
    }

    @Test
    @DisplayName("should generate due next month PDF")
    void shouldGenerateDueNextMonthPdf()
            throws Exception {
        mockMvc.perform(get("/reports/due-next-month")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(
                        "application/pdf"));
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
}

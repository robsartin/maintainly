package solutions.mystuff.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Security Configuration")
class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should allow unauthenticated health endpoint")
    void shouldAllowHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should not block unauthenticated prometheus")
    void shouldNotBlockPrometheus() throws Exception {
        assertNotRedirectedToLogin(
                "/actuator/prometheus");
    }

    @Test
    @DisplayName("should not block unauthenticated api-docs")
    void shouldNotBlockApiDocs() throws Exception {
        assertNotRedirectedToLogin("/api-docs");
    }

    @Test
    @DisplayName("should not block unauthenticated swagger-ui")
    void shouldNotBlockSwaggerUi() throws Exception {
        assertNotRedirectedToLogin(
                "/swagger-ui/index.html");
    }

    @Test
    @DisplayName("should redirect unauthenticated to login")
    void shouldRedirectUnauthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("should allow authenticated access")
    void shouldAllowAuthenticated() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should set X-Frame-Options DENY")
    void shouldSetFrameOptionsDeny() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(header().string(
                        "X-Frame-Options", "DENY"));
    }

    @Test
    @DisplayName("should set X-Content-Type-Options nosniff")
    void shouldSetContentTypeOptions() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(header().string(
                        "X-Content-Type-Options",
                        "nosniff"));
    }

    @Test
    @DisplayName("should set Content-Security-Policy")
    void shouldSetCsp() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(header().exists(
                        "Content-Security-Policy"));
    }

    @Test
    @DisplayName("CSP should not allow unsafe-inline scripts")
    void cspShouldBlockInlineScripts()
            throws Exception {
        String csp = mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andReturn().getResponse()
                .getHeader("Content-Security-Policy");
        if (csp.contains("'unsafe-inline'")
                && csp.indexOf("'unsafe-inline'")
                < csp.indexOf("style-src")) {
            throw new AssertionError(
                    "CSP should not allow"
                    + " unsafe-inline scripts");
        }
    }

    @Test
    @DisplayName("CSP should allow unpkg.com for HTMX")
    void cspShouldAllowHtmxCdn() throws Exception {
        String csp = mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andReturn().getResponse()
                .getHeader("Content-Security-Policy");
        if (!csp.contains("https://unpkg.com")) {
            throw new AssertionError(
                    "CSP script-src should allow"
                    + " https://unpkg.com for HTMX");
        }
    }

    @Test
    @DisplayName("should set Referrer-Policy")
    void shouldSetReferrerPolicy() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(header().string(
                        "Referrer-Policy",
                        "strict-origin-when-cross-origin"));
    }

    private void assertNotRedirectedToLogin(String path)
            throws Exception {
        int status = mockMvc.perform(get(path))
                .andReturn().getResponse().getStatus();
        if (status == 302) {
            throw new AssertionError(
                    "Expected " + path
                    + " not to redirect to login"
                    + " but got 302");
        }
    }
}

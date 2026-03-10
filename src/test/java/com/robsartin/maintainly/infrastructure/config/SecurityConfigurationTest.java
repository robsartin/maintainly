package com.robsartin.maintainly.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk());
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

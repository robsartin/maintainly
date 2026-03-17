package solutions.mystuff.application.web.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("API Security Configuration Integration")
class ApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should allow token endpoint without auth")
    void shouldAllowTokenEndpointWithoutAuth()
            throws Exception {
        mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"dev\","
                                + "\"password\":\"dev\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should require auth for API endpoints")
    void shouldRequireAuthForApiEndpoints()
            throws Exception {
        mockMvc.perform(get("/api/nonexistent"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should not require CSRF for API endpoints")
    void shouldNotRequireCsrfForApi() throws Exception {
        mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"dev\","
                                + "\"password\":\"dev\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should use stateless session for API")
    void shouldUseStatelessSession() throws Exception {
        mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"dev\","
                                + "\"password\":\"dev\"}"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String setCookie = result.getResponse()
                            .getHeader("Set-Cookie");
                    if (setCookie != null) {
                        org.junit.jupiter.api.Assertions
                                .assertFalse(
                                setCookie.contains(
                                        "JSESSIONID"));
                    }
                });
    }
}

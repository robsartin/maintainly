package solutions.mystuff.application.web.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Item API Controller Integration")
class ItemApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("should return items as JSON with valid token")
    void shouldReturnItemsWhenAuthenticated()
            throws Exception {
        String token = obtainToken();
        mockMvc.perform(get("/api/items")
                        .header("Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("should reject unauthenticated list request")
    void shouldRejectUnauthenticatedListRequest()
            throws Exception {
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("should return 404 for nonexistent item")
    void shouldReturn404WhenItemNotFound()
            throws Exception {
        String token = obtainToken();
        String fakeId =
                "00000000-0000-0000-0000-000000000000";
        mockMvc.perform(get("/api/items/" + fakeId)
                        .header("Authorization",
                                "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("should return item detail as JSON")
    void shouldReturnItemDetailWhenExists()
            throws Exception {
        createItemViaUi();
        String token = obtainToken();
        MvcResult listResult = mockMvc
                .perform(get("/api/items")
                        .header("Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode items = mapper.readTree(
                listResult.getResponse()
                        .getContentAsString());
        if (items.isEmpty()) {
            return;
        }
        String id = items.get(0).get("id").asText();
        mockMvc.perform(get("/api/items/" + id)
                        .header("Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",
                        notNullValue()));
    }

    private String obtainToken() throws Exception {
        MvcResult result = mockMvc
                .perform(post("/api/auth/token")
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"dev\","
                                + "\"password\":\"dev\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = mapper.readTree(
                result.getResponse()
                        .getContentAsString());
        return body.get("token").asText();
    }

    private void createItemViaUi() throws Exception {
        mockMvc.perform(post("/items")
                .param("name", "API Test Item")
                .with(user("dev").roles("USER"))
                .with(csrf()));
    }
}

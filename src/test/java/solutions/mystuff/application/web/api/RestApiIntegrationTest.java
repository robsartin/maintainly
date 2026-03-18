package solutions.mystuff.application.web.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure
        .AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.status;

/**
 * Integration tests for the v1 REST API controllers.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     Test->>AuthController: POST /api/auth/token
 *     AuthController-->>Test: JWT
 *     Test->>ItemApiController: GET /api/v1/items
 *     ItemApiController-->>Test: 200 JSON
 * </div>
 *
 * @see ItemApiController
 * @see VendorApiController
 * @see ScheduleApiController
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("REST API Integration")
class RestApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/api/auth/token")
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content(
                                "{\"username\":\"dev\","
                                + "\"password\":\"dev\"}"))
                .andReturn();
        String body = result.getResponse()
                .getContentAsString();
        token = body.replaceAll(
                ".*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    @DisplayName("should list items")
    void shouldListItems() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                        .header("Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",
                        notNullValue()))
                .andExpect(jsonPath("$.content.length()",
                        greaterThan(0)));
    }

    @Test
    @DisplayName("should get item by ID")
    void shouldGetItemById() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(get("/api/v1/items/" + itemId)
                        .header("Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",
                        notNullValue()));
    }

    @Test
    @DisplayName("should create item")
    void shouldCreateItem() throws Exception {
        mockMvc.perform(post("/api/v1/items")
                        .header("Authorization",
                                "Bearer " + token)
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"API Test"
                                + " Item\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name")
                        .value("API Test Item"));
    }

    @Test
    @DisplayName("should update item")
    void shouldUpdateItem() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(put("/api/v1/items/" + itemId)
                        .header("Authorization",
                                "Bearer " + token)
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated"
                                + " via API\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Updated via API"));
    }

    @Test
    @DisplayName("should return 404 for unknown item")
    void shouldReturn404ForUnknownItem()
            throws Exception {
        String fakeId =
                "00000000-0000-0000-0000-000000000000";
        mockMvc.perform(get("/api/v1/items/" + fakeId)
                        .header("Authorization",
                                "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should list vendors")
    void shouldListVendors() throws Exception {
        mockMvc.perform(get("/api/v1/vendors")
                        .header("Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",
                        greaterThan(0)));
    }

    @Test
    @DisplayName("should create vendor")
    void shouldCreateVendor() throws Exception {
        mockMvc.perform(post("/api/v1/vendors")
                        .header("Authorization",
                                "Bearer " + token)
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"API Vendor"
                                + "\",\"altPhones\":[]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name")
                        .value("API Vendor"));
    }

    @Test
    @DisplayName("should list schedules")
    void shouldListSchedules() throws Exception {
        mockMvc.perform(get("/api/v1/schedules")
                        .header("Authorization",
                                "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",
                        notNullValue()));
    }

    @Test
    @DisplayName("should require auth for API")
    void shouldRequireAuthForApi() throws Exception {
        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isUnauthorized());
    }

    private String getFirstItemId() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/api/v1/items")
                        .header("Authorization",
                                "Bearer " + token))
                .andReturn();
        String body = result.getResponse()
                .getContentAsString();
        return body.replaceAll(
                ".*?\"id\":\"([^\"]+)\".*", "$1");
    }
}

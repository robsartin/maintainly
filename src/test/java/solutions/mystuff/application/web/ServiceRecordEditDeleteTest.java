package solutions.mystuff.application.web;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure
        .AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions
        .assertFalse;
import static org.junit.jupiter.api.Assertions
        .assertTrue;
import static org.springframework.security.test.web
        .servlet.request
        .SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web
        .servlet.request
        .SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.status;

/**
 * Integration tests for service record editing and
 * deletion via the item controller endpoints.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     participant Test
 *     participant Controller
 *     Test->>Controller: PUT /items/{id}/records/{rid}
 *     Controller-->>Test: 302 redirect
 *     Test->>Controller: DELETE /items/{id}/records/{rid}
 *     Controller-->>Test: 302 redirect
 * </div>
 *
 * @see ItemController
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Service Record Edit/Delete Integration")
class ServiceRecordEditDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should update a service record")
    void shouldUpdateServiceRecord() throws Exception {
        String[] ids = createRecordAndGetIds();
        String itemId = ids[0];
        String recordId = ids[1];

        mockMvc.perform(put("/items/" + itemId
                        + "/records/" + recordId)
                        .param("summary", "Updated work")
                        .param("serviceDate", "2026-07-01")
                        .param("techName", "Bob")
                        .param("cost", "99.50")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/items/" + itemId));
    }

    @Test
    @DisplayName("should reject blank summary on update")
    void shouldRejectBlankSummaryOnUpdate()
            throws Exception {
        String[] ids = createRecordAndGetIds();
        mockMvc.perform(put("/items/" + ids[0]
                        + "/records/" + ids[1])
                        .param("summary", "  ")
                        .param("serviceDate", "2026-07-01")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject invalid date on update")
    void shouldRejectInvalidDateOnUpdate()
            throws Exception {
        String[] ids = createRecordAndGetIds();
        mockMvc.perform(put("/items/" + ids[0]
                        + "/records/" + ids[1])
                        .param("summary", "Test")
                        .param("serviceDate", "bad-date")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName(
            "should return 404 for unknown record update")
    void shouldReturn404ForUnknownRecordUpdate()
            throws Exception {
        String itemId = getFirstItemId();
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(put("/items/" + itemId
                        + "/records/" + fakeId)
                        .param("summary", "Test")
                        .param("serviceDate", "2026-07-01")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should delete a service record")
    void shouldDeleteServiceRecord() throws Exception {
        String[] ids = createRecordAndGetIds();
        String itemId = ids[0];
        String recordId = ids[1];

        mockMvc.perform(delete("/items/" + itemId
                        + "/records/" + recordId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/items/" + itemId));
    }

    @Test
    @DisplayName(
            "should return 404 for unknown record delete")
    void shouldReturn404ForUnknownRecordDelete()
            throws Exception {
        String itemId = getFirstItemId();
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(delete("/items/" + itemId
                        + "/records/" + fakeId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should render edit button for records")
    void shouldRenderEditButtonForRecords()
            throws Exception {
        String[] ids = createRecordAndGetIds();
        mockMvc.perform(get("/items/" + ids[0])
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "edit-record-")));
    }

    @Test
    @DisplayName(
            "should render delete button for records")
    void shouldRenderDeleteButtonForRecords()
            throws Exception {
        String[] ids = createRecordAndGetIds();
        MvcResult result = mockMvc.perform(
                get("/items/" + ids[0])
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(
                html.contains(
                        "Delete this service record?"),
                "should have delete confirm");
    }

    @Test
    @DisplayName(
            "should render record edit form with values")
    void shouldRenderRecordEditForm()
            throws Exception {
        String[] ids = createRecordAndGetIds();
        MvcResult result = mockMvc.perform(
                get("/items/" + ids[0])
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(html.contains("_method"),
                "edit form should use _method PUT");
    }

    @Test
    @DisplayName("should verify record not visible after"
            + " delete")
    void shouldNotShowDeletedRecord() throws Exception {
        String[] ids = createRecordAndGetIds();
        String itemId = ids[0];
        String recordId = ids[1];

        mockMvc.perform(delete("/items/" + itemId
                        + "/records/" + recordId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()));

        MvcResult result = mockMvc.perform(
                get("/items/" + itemId)
                        .with(user("dev").roles("USER")))
                .andReturn();
        @SuppressWarnings("unchecked")
        List<ServiceRecord> records =
                (List<ServiceRecord>)
                        result.getModelAndView()
                                .getModel()
                                .get("itemRecords");
        boolean found = records.stream()
                .anyMatch(r -> r.getId().toString()
                        .equals(recordId));
        assertFalse(found,
                "deleted record should not appear");
    }

    private String[] createRecordAndGetIds()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/" + itemId
                        + "/service-records")
                        .param("summary", "Test record")
                        .param("serviceDate", "2026-06-01")
                        .param("oneOff", "true")
                        .with(user("dev").roles("USER"))
                        .with(csrf()));
        String recordId = getFirstRecordId(itemId);
        return new String[]{itemId, recordId};
    }

    @SuppressWarnings("unchecked")
    private String getFirstItemId() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/items")
                        .with(user("dev").roles("USER")))
                .andReturn();
        List<Item> items = (List<Item>)
                result.getModelAndView().getModel()
                        .get("items");
        return items.get(0).getId().toString();
    }

    @SuppressWarnings("unchecked")
    private String getFirstRecordId(String itemId)
            throws Exception {
        MvcResult result = mockMvc.perform(
                get("/items/" + itemId)
                        .with(user("dev").roles("USER")))
                .andReturn();
        List<ServiceRecord> records =
                (List<ServiceRecord>)
                        result.getModelAndView()
                                .getModel()
                                .get("itemRecords");
        return records.get(0).getId().toString();
    }
}

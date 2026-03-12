package solutions.mystuff.application.web;

import java.util.List;
import java.util.UUID;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceSchedule;
import solutions.mystuff.domain.model.Vendor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions
        .assertTrue;
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
@DisplayName("Item Controller Integration")
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should redirect root to schedules")
    void shouldRedirectRootToSchedules() throws Exception {
        mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules"));
    }

    @Test
    @DisplayName("should add a new item")
    void shouldAddNewItem() throws Exception {
        mockMvc.perform(post("/items/add")
                        .param("name", "Test Widget")
                        .param("location", "Garage")
                        .param("manufacturer", "Acme")
                        .param("modelName", "W-100")
                        .param("serialNumber", "SN-999")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("should add item with name only")
    void shouldAddItemNameOnly() throws Exception {
        mockMvc.perform(post("/items/add")
                        .param("name", "Minimal Item")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("should render paginated item list")
    void shouldRenderItemList() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists(
                        "itemPage"))
                .andExpect(model().attributeExists(
                        "username"))
                .andExpect(model().attributeExists(
                        "organization"));
    }

    @Test
    @DisplayName("should limit items to page size")
    void shouldLimitItemsToPageSize() throws Exception {
        mockMvc.perform(get("/items")
                        .param("size", "5")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("items",
                        hasSize(lessThanOrEqualTo(5))));
    }

    @Test
    @DisplayName("should clamp page size to max 50")
    void shouldClampPageSizeToMax() throws Exception {
        mockMvc.perform(get("/items")
                        .param("size", "100")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("items",
                        hasSize(lessThanOrEqualTo(50))));
    }

    @Test
    @DisplayName("should include Link header for items")
    void shouldIncludeLinkHeader() throws Exception {
        mockMvc.perform(get("/items")
                        .param("size", "2")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(header().string("Link",
                        containsString("rel=\"first\"")))
                .andExpect(header().string("Link",
                        containsString("rel=\"last\"")))
                .andExpect(header().string("Link",
                        containsString("rel=\"next\"")));
    }

    @Test
    @DisplayName("should navigate to second item page")
    void shouldNavigateToSecondPage() throws Exception {
        mockMvc.perform(get("/items")
                        .param("page", "1")
                        .param("size", "2")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "items"));
    }

    @Test
    @DisplayName("should search items with pagination")
    void shouldSearchItems() throws Exception {
        mockMvc.perform(get("/items")
                        .param("q", "Furnace")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("q",
                        "Furnace"));
    }

    @Test
    @DisplayName("should return all items with blank search")
    void shouldReturnAllWithBlankSearch()
            throws Exception {
        mockMvc.perform(get("/items")
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
        mockMvc.perform(post("/items/log")
                        .param("itemId", itemId)
                        .param("summary", "Filter replaced")
                        .param("serviceDate", "2026-04-15")
                        .param("techName", "Jane")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("should log item service without tech")
    void shouldLogItemServiceNoTech() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/log")
                        .param("itemId", itemId)
                        .param("summary", "Quick check")
                        .param("serviceDate", "2026-04-16")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("should handle invalid item ID on log")
    void shouldHandleInvalidItemId() throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/items/log")
                        .param("itemId", fakeId.toString())
                        .param("summary", "Test")
                        .param("serviceDate", "2026-04-15")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should schedule service from item")
    void shouldScheduleFromItem() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/schedule")
                        .param("itemId", itemId)
                        .param("serviceType",
                                "HVAC Inspection")
                        .param("nextDueDate", "2026-09-15")
                        .param("frequencyInterval", "6")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("should handle invalid item on schedule")
    void shouldHandleInvalidItemSchedule()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/items/schedule")
                        .param("itemId",
                                fakeId.toString())
                        .param("serviceType",
                                "HVAC Inspection")
                        .param("nextDueDate", "2026-09-15")
                        .param("frequencyInterval", "6")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should provide form data attributes")
    void shouldProvideFormData() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "vendors"))
                .andExpect(model().attributeExists(
                        "frequencyUnits"));
    }

    @Test
    @DisplayName("should schedule with existing vendor")
    void shouldScheduleWithExistingVendor()
            throws Exception {
        String itemId = getFirstItemId();
        String vendorId = getFirstVendorId();
        mockMvc.perform(post("/items/schedule")
                        .param("itemId", itemId)
                        .param("serviceType",
                                "Plumbing Check")
                        .param("nextDueDate", "2026-10-01")
                        .param("frequencyInterval", "3")
                        .param("frequencyUnit", "months")
                        .param("vendorId", vendorId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("should schedule with new vendor")
    void shouldScheduleWithNewVendor()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/schedule")
                        .param("itemId", itemId)
                        .param("serviceType",
                                "Filter Replacement")
                        .param("nextDueDate", "2026-11-01")
                        .param("frequencyInterval", "1")
                        .param("frequencyUnit", "years")
                        .param("vendorId", "__new__")
                        .param("newVendorName",
                                "New Test Vendor")
                        .param("newVendorPhone",
                                "555-9999")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("should reject new vendor without name")
    void shouldRejectNewVendorNoName()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/schedule")
                        .param("itemId", itemId)
                        .param("serviceType",
                                "General Maintenance")
                        .param("nextDueDate", "2026-11-01")
                        .param("frequencyInterval", "1")
                        .param("frequencyUnit", "years")
                        .param("vendorId", "__new__")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should show item detail with history")
    void shouldShowItemDetail() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(get("/items/detail")
                        .param("itemId", itemId)
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "selectedItemId"))
                .andExpect(model().attributeExists(
                        "itemRecords"))
                .andExpect(model().attributeExists(
                        "itemSchedules"))
                .andExpect(model().attributeExists(
                        "items"));
    }

    @Test
    @DisplayName("should handle invalid item on detail")
    void shouldHandleInvalidItemDetail()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(get("/items/detail")
                        .param("itemId",
                                fakeId.toString())
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "itemRecords"));
    }

    @Test
    @DisplayName("should render data-target for log button")
    void shouldRenderLogDataTarget() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "data-target=\"item-log-")));
    }

    @Test
    @DisplayName("should render data-target for schedule button")
    void shouldRenderScheduleDataTarget()
            throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "data-target=\"item-sched-")));
    }

    @Test
    @DisplayName("should use external JS not inline handlers")
    void shouldUseExternalJs() throws Exception {
        MvcResult result = mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(html.contains("/js/app.js"),
                "should include external app.js");
        assertTrue(!html.contains("onclick="),
                "should have no inline onclick");
        assertTrue(!html.contains("onchange="),
                "should have no inline onchange");
        assertTrue(html.contains(
                "data-toggle-form=\"add-item-form\""),
                "should have data-toggle-form");
    }

    @Test
    @DisplayName("should serve app.js as static resource")
    void shouldServeAppJs() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/js/app.js")
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String js = result.getResponse()
                .getContentAsString();
        assertTrue(
                js.contains("data-toggle-form"),
                "app.js should handle data-toggle-form");
        assertTrue(
                js.contains("data-target"),
                "app.js should handle data-target");
        assertTrue(
                js.contains("data-navigate"),
                "app.js should handle data-navigate");
        assertTrue(
                js.contains("data-confirm-submit"),
                "app.js should handle confirms");
        assertTrue(
                js.contains("data-vendor-change"),
                "app.js should handle vendor change");
        assertTrue(
                !js.contains("stopPropagation"),
                "app.js must not use stopPropagation"
                + " (breaks event delegation)");
    }

    @Test
    @DisplayName("should render data-navigate for item rows")
    void shouldRenderDataNavigate() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "data-navigate=")));
    }

    @Test
    @DisplayName("should render nav icons in items page")
    void shouldRenderNavIcons() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("class=\"nav-link\"")))
                .andExpect(content().string(
                        containsString("<svg")));
    }

    @Test
    @DisplayName("should complete a scheduled service")
    void shouldCompleteSchedule() throws Exception {
        String scheduleId = getFirstScheduleId();
        mockMvc.perform(post("/items/complete")
                        .param("scheduleId", scheduleId)
                        .param("summary", "Routine check")
                        .param("serviceDate", "2026-03-10")
                        .param("techName", "John")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("should handle invalid schedule on complete")
    void shouldHandleInvalidComplete()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/items/complete")
                        .param("scheduleId",
                                fakeId.toString())
                        .param("summary", "Test")
                        .param("serviceDate", "2026-03-10")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should skip a scheduled service")
    void shouldSkipSchedule() throws Exception {
        String scheduleId = getFirstScheduleId();
        mockMvc.perform(post("/items/skip")
                        .param("scheduleId", scheduleId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("should handle invalid schedule on skip")
    void shouldHandleInvalidSkip() throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/items/skip")
                        .param("scheduleId",
                                fakeId.toString())
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should show no-org for new user")
    void shouldShowNoOrgForNewUser() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("unknown").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute(
                        "noOrganization", true));
    }

    @Test
    @DisplayName("should reject blank item name")
    void shouldRejectBlankItemName() throws Exception {
        mockMvc.perform(post("/items/add")
                        .param("name", "  ")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject item name exceeding"
            + " max length")
    void shouldRejectLongItemName() throws Exception {
        String longName = "x".repeat(201);
        mockMvc.perform(post("/items/add")
                        .param("name", longName)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject blank summary on log")
    void shouldRejectBlankSummary() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/log")
                        .param("itemId", itemId)
                        .param("summary", "  ")
                        .param("serviceDate", "2026-03-10")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject invalid service date")
    void shouldRejectInvalidDate() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/log")
                        .param("itemId", itemId)
                        .param("summary", "Test")
                        .param("serviceDate", "not-valid")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject zero frequency interval")
    void shouldRejectZeroInterval() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/schedule")
                        .param("itemId", itemId)
                        .param("serviceType", "Test")
                        .param("nextDueDate", "2026-06-01")
                        .param("frequencyInterval", "0")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject blank service type"
            + " on schedule")
    void shouldRejectBlankServiceType()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/schedule")
                        .param("itemId", itemId)
                        .param("serviceType", "  ")
                        .param("nextDueDate", "2026-06-01")
                        .param("frequencyInterval", "1")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @SuppressWarnings("unchecked")
    private String getFirstItemId() throws Exception {
        return ((List<Item>) getModel("items"))
                .get(0).getId().toString();
    }

    @SuppressWarnings("unchecked")
    private String getFirstScheduleId()
            throws Exception {
        String itemId = getFirstItemId();
        MvcResult result = mockMvc.perform(
                get("/items/detail")
                        .param("itemId", itemId)
                        .with(user("dev").roles("USER")))
                .andReturn();
        List<ServiceSchedule> schedules =
                (List<ServiceSchedule>)
                        result.getModelAndView()
                                .getModel()
                                .get("itemSchedules");
        return schedules.get(0).getId().toString();
    }

    @SuppressWarnings("unchecked")
    private String getFirstVendorId()
            throws Exception {
        return ((List<Vendor>)
                getModel("vendors"))
                .get(0).getId().toString();
    }

    private Object getModel(String attr)
            throws Exception {
        MvcResult result = mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andReturn();
        return result.getModelAndView().getModel()
                .get(attr);
    }
}

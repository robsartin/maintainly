package solutions.mystuff.application.web;

import java.time.LocalDate;
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
        .assertEquals;
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
        mockMvc.perform(post("/items")
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
        mockMvc.perform(post("/items")
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
    @DisplayName("should show search cancel when searching")
    void shouldShowSearchCancel() throws Exception {
        MvcResult result = mockMvc.perform(get("/items")
                        .param("q", "Furnace")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(
                html.contains("Clear search"),
                "should show clear search button");
    }

    @Test
    @DisplayName("should hide search cancel without query")
    void shouldHideSearchCancel() throws Exception {
        MvcResult result = mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(
                !html.contains("Clear search"),
                "should not show clear search button");
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
        mockMvc.perform(post("/items/" + itemId
                        + "/service-records")
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
        mockMvc.perform(post("/items/" + itemId
                        + "/service-records")
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
        mockMvc.perform(post("/items/" + fakeId
                        + "/service-records")
                        .param("summary", "Test")
                        .param("serviceDate", "2026-04-15")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should schedule service from item")
    void shouldScheduleFromItem() throws Exception {
        String itemId = getFirstItemId();
        String vendorId = getFirstVendorId();
        mockMvc.perform(post("/items/" + itemId
                        + "/schedules")
                        .param("serviceType",
                                "HVAC Inspection")
                        .param("nextDueDate", "2026-09-15")
                        .param("frequencyInterval", "6")
                        .param("frequencyUnit", "months")
                        .param("vendorId", vendorId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("should reject schedule without vendor")
    void shouldRejectScheduleWithoutVendor()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/" + itemId
                        + "/schedules")
                        .param("serviceType",
                                "HVAC Inspection")
                        .param("nextDueDate", "2026-09-15")
                        .param("frequencyInterval", "6")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should handle invalid item on schedule")
    void shouldHandleInvalidItemSchedule()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/items/" + fakeId
                        + "/schedules")
                        .param("serviceType",
                                "HVAC Inspection")
                        .param("nextDueDate", "2026-09-15")
                        .param("frequencyInterval", "6")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
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
        mockMvc.perform(post("/items/" + itemId
                        + "/schedules")
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
        mockMvc.perform(post("/items/" + itemId
                        + "/schedules")
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
        mockMvc.perform(post("/items/" + itemId
                        + "/schedules")
                        .param("serviceType",
                                "General Maintenance")
                        .param("nextDueDate", "2026-11-01")
                        .param("frequencyInterval", "1")
                        .param("frequencyUnit", "years")
                        .param("vendorId", "__new__")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should show item detail with history")
    void shouldShowItemDetail() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(get("/items/" + itemId)
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
        mockMvc.perform(get("/items/" + fakeId)
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "itemRecords"));
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
        mockMvc.perform(post("/schedules/" + scheduleId
                        + "/completions")
                        .param("summary", "Routine check")
                        .param("serviceDate", "2026-03-10")
                        .param("techName", "John")
                        .param("redirectTo", "item")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("should handle invalid schedule on complete")
    void shouldHandleInvalidComplete()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/schedules/" + fakeId
                        + "/completions")
                        .param("summary", "Test")
                        .param("serviceDate", "2026-03-10")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("should skip a scheduled service")
    void shouldSkipSchedule() throws Exception {
        String scheduleId = getFirstScheduleId();
        mockMvc.perform(post("/schedules/" + scheduleId
                        + "/skip")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("should render edit button for item schedules")
    void shouldRenderEditButtonForItemSchedules()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(get("/items/" + itemId)
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "data-target=\"edit-item-sched-")));
    }

    @Test
    @DisplayName("should edit schedule from item detail")
    void shouldEditScheduleFromItemDetail()
            throws Exception {
        String scheduleId = getFirstScheduleId();
        String vendorId = getFirstVendorId();
        mockMvc.perform(post("/schedules/" + scheduleId)
                        .param("serviceType",
                                "Edited From Items")
                        .param("nextDueDate", "2027-06-01")
                        .param("frequencyInterval", "2")
                        .param("frequencyUnit", "years")
                        .param("vendorId", vendorId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules"));
    }

    @Test
    @DisplayName("should handle invalid schedule on skip")
    void shouldHandleInvalidSkip() throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(post("/schedules/" + fakeId
                        + "/skip")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
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
        mockMvc.perform(post("/items")
                        .param("name", "  ")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject item name exceeding"
            + " max length")
    void shouldRejectLongItemName() throws Exception {
        String longName = "x".repeat(201);
        mockMvc.perform(post("/items")
                        .param("name", longName)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject blank summary on log")
    void shouldRejectBlankSummary() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/" + itemId
                        + "/service-records")
                        .param("summary", "  ")
                        .param("serviceDate", "2026-03-10")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject invalid service date")
    void shouldRejectInvalidDate() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/" + itemId
                        + "/service-records")
                        .param("summary", "Test")
                        .param("serviceDate", "not-valid")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject zero frequency interval")
    void shouldRejectZeroInterval() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/" + itemId
                        + "/schedules")
                        .param("serviceType", "Test")
                        .param("nextDueDate", "2026-06-01")
                        .param("frequencyInterval", "0")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject blank service type"
            + " on schedule")
    void shouldRejectBlankServiceType()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/" + itemId
                        + "/schedules")
                        .param("serviceType", "  ")
                        .param("nextDueDate", "2026-06-01")
                        .param("frequencyInterval", "1")
                        .param("frequencyUnit", "months")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should render one-off service button")
    void shouldRenderOneOffServiceButton()
            throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "data-target=\"item-oneoff-")));
    }

    @Test
    @DisplayName("should log one-off without advancing"
            + " schedule")
    void shouldLogOneOffService() throws Exception {
        String itemId = getFirstItemId();
        LocalDate dueBefore =
                getNextDueDateForItem(itemId);
        mockMvc.perform(post("/items/" + itemId
                        + "/service-records")
                        .param("summary", "One-off repair")
                        .param("serviceDate", "2026-05-01")
                        .param("oneOff", "true")
                        .param("techName", "Mike")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
        LocalDate dueAfter =
                getNextDueDateForItem(itemId);
        assertEquals(dueBefore, dueAfter,
                "one-off should not advance schedule");
    }

    @Test
    @DisplayName("should advance schedule on log service")
    void shouldAdvanceScheduleOnLog() throws Exception {
        String itemId = getFirstItemId();
        LocalDate dueBefore =
                getNextDueDateForItem(itemId);
        mockMvc.perform(post("/items/" + itemId
                        + "/service-records")
                        .param("summary", "Routine service")
                        .param("serviceDate", "2026-06-01")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
        LocalDate dueAfter =
                getNextDueDateForItem(itemId);
        assertTrue(dueAfter.isAfter(dueBefore),
                "log should advance next due date");
    }

    @Test
    @DisplayName("one-off form should include oneOff param")
    void shouldRenderOneOffFormAction() throws Exception {
        MvcResult result = mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(
                html.contains("item-oneoff-"),
                "should have one-off form row");
        assertTrue(
                html.contains("One-off service"),
                "should have one-off service title");
        assertTrue(
                html.contains(
                        "name=\"oneOff\" value=\"true\""),
                "one-off form should have oneOff param");
    }

    @Test
    @DisplayName("should render cancel buttons on forms")
    void shouldRenderCancelButtons() throws Exception {
        MvcResult result = mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(html.contains("btn-cancel"),
                "should have btn-cancel class");
        assertTrue(html.contains(
                "data-toggle-form=\"add-item-form\""),
                "should have cancel for add form");
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
                get("/items/" + itemId)
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

    @SuppressWarnings("unchecked")
    private LocalDate getNextDueDateForItem(
            String itemId) throws Exception {
        MvcResult result = mockMvc.perform(
                get("/items/" + itemId)
                        .with(user("dev").roles("USER")))
                .andReturn();
        List<ServiceSchedule> schedules =
                (List<ServiceSchedule>)
                        result.getModelAndView()
                                .getModel()
                                .get("itemSchedules");
        return schedules.stream()
                .filter(ServiceSchedule::isActive)
                .map(ServiceSchedule::getNextDueDate)
                .min(LocalDate::compareTo)
                .orElse(null);
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

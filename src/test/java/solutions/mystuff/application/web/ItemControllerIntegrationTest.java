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
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Item Controller Integration")
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should render dashboard at root")
    void shouldRenderDashboardAtRoot() throws Exception {
        mockMvc.perform(get("/")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"));
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
                html.contains("Clear filters"),
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
                !html.contains("Clear filters"),
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
        String itemId = getItemIdWithSchedule();
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
        String itemId = createItemWithSchedule();
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

    @Test
    @DisplayName("should update an existing item")
    void shouldUpdateItem() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(put("/items/" + itemId)
                        .param("name", "Updated Widget")
                        .param("location", "Kitchen")
                        .param("category", "Appliance")
                        .param("modelNumber", "MN-42")
                        .param("modelYear", "2025")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("should reject blank name on update")
    void shouldRejectBlankNameOnUpdate()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(put("/items/" + itemId)
                        .param("name", "  ")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should handle invalid item on update")
    void shouldHandleInvalidItemOnUpdate()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(put("/items/" + fakeId)
                        .param("name", "Test")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should render edit button for items")
    void shouldRenderEditButtonForItems()
            throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "data-target=\"edit-item-")));
    }

    @Test
    @DisplayName("should render category column")
    void shouldRenderCategoryColumn()
            throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "<th>Category</th>")));
    }

    @Test
    @DisplayName("should render purchase date column")
    void shouldRenderPurchaseDateColumn()
            throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "<th>Purchased</th>")));
    }

    @Test
    @DisplayName("should render category datalist")
    void shouldRenderCategoryDatalist()
            throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "id=\"category-suggestions"
                        )));
    }

    @Test
    @DisplayName("should filter items by category")
    void shouldFilterItemsByCategory()
            throws Exception {
        mockMvc.perform(get("/items")
                        .param("category", "HVAC")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute(
                        "selectedCategory", "HVAC"));
    }

    @Test
    @DisplayName("should provide categories model attr")
    void shouldProvideCategoriesAttribute()
            throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "categories"));
    }

    @Test
    @DisplayName("should filter by category and search")
    void shouldFilterByCategoryAndSearch()
            throws Exception {
        mockMvc.perform(get("/items")
                        .param("q", "Furnace")
                        .param("category", "HVAC")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("q",
                        "Furnace"))
                .andExpect(model().attribute(
                        "selectedCategory", "HVAC"));
    }

    @Test
    @DisplayName("should ignore blank category filter")
    void shouldIgnoreBlankCategory()
            throws Exception {
        mockMvc.perform(get("/items")
                        .param("category", "  ")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeDoesNotExist(
                        "selectedCategory"));
    }

    @Test
    @DisplayName("should render category filter dropdown")
    void shouldRenderCategoryDropdown()
            throws Exception {
        MvcResult result = mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(
                html.contains("All Categories"),
                "should show All Categories option");
        assertTrue(
                html.contains("name=\"category\""),
                "should have category select");
    }

    @Test
    @DisplayName("should delete item")
    void shouldDeleteItem() throws Exception {
        mockMvc.perform(post("/items")
                .param("name", "DeleteMe Item")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        String itemId =
                getItemIdByName("DeleteMe Item");

        mockMvc.perform(delete("/items/" + itemId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    @DisplayName("should return 404 when deleting"
            + " nonexistent item")
    void shouldReturn404WhenDeletingMissing()
            throws Exception {
        UUID fakeId = UUID.randomUUID();
        mockMvc.perform(delete("/items/" + fakeId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should render delete button for items")
    void shouldRenderDeleteButtonForItems()
            throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "Delete item")));
    }

    @Test

    @DisplayName("should reject model year below 1900")
    void shouldRejectModelYearBelowBound()
            throws Exception {
        mockMvc.perform(post("/items")
                        .param("name", "Test Item")
                        .param("modelYear", "1899")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject model year above 2100")
    void shouldRejectModelYearAboveBound()
            throws Exception {
        mockMvc.perform(post("/items")
                        .param("name", "Test Item")
                        .param("modelYear", "2101")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject model year on update"
            + " below 1900")
    void shouldRejectModelYearOnUpdateBelowBound()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(put("/items/" + itemId)
                        .param("name", "Test")
                        .param("modelYear", "1899")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));
    }

    @Test
    @DisplayName("should reject negative cost on"
            + " service record")
    void shouldRejectNegativeCostOnServiceRecord()
            throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(post("/items/" + itemId
                        + "/service-records")
                        .param("summary", "Test service")
                        .param("serviceDate", "2026-04-15")
                        .param("cost", "-1.00")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(model().attributeExists(
                        "error"));

    }

    @Test
    @DisplayName("should create item with all fields")
    void shouldCreateItemWithAllFields()
            throws Exception {
        mockMvc.perform(post("/items")
                        .param("name", "Full Item")
                        .param("location", "Basement")
                        .param("manufacturer", "Acme")
                        .param("modelName", "Z-100")
                        .param("modelNumber", "MN-100")
                        .param("modelYear", "2024")
                        .param("serialNumber", "SN-001")
                        .param("category", "Plumbing")
                        .param("purchaseDate",
                                "2024-06-15")
                        .param("notes", "Test notes")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @SuppressWarnings("unchecked")
    private String getFirstItemId() throws Exception {
        return ((List<Item>) getModel("items"))
                .get(0).getId().toString();
    }

    private String createItemWithSchedule()
            throws Exception {
        mockMvc.perform(post("/items")
                .param("name", "Advance Test Item")
                .with(user("dev").roles("USER"))
                .with(csrf()));
        String itemId = getItemIdByName(
                "Advance Test Item");
        String vendorId = getFirstVendorId();
        String due = LocalDate.now().plusDays(7)
                .toString();
        mockMvc.perform(post("/items/" + itemId
                        + "/schedules")
                .param("serviceType", "Test Svc")
                .param("nextDueDate", due)
                .param("frequencyInterval", "6")
                .param("frequencyUnit", "months")
                .param("vendorId", vendorId)
                .with(user("dev").roles("USER"))
                .with(csrf()));
        return itemId;
    }

    @SuppressWarnings("unchecked")
    private String getItemIdByName(String name)
            throws Exception {
        List<Item> items =
                (List<Item>) getModel("items");
        return items.stream()
                .filter(i -> name.equals(i.getName()))
                .findFirst()
                .orElseThrow()
                .getId().toString();
    }

    @SuppressWarnings("unchecked")
    private String getItemIdWithSchedule()
            throws Exception {
        List<Item> items =
                (List<Item>) getModel("items");
        for (Item item : items) {
            LocalDate due = getNextDueDateForItem(
                    item.getId().toString());
            if (due != null) {
                return item.getId().toString();
            }
        }
        return items.get(0).getId().toString();
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

    @Test
    @DisplayName("should render items page HTML"
            + " without template errors")
    void shouldRenderItemsPageHtml() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("<table")))
                .andExpect(content().string(
                        containsString("All Categories")))
                .andExpect(content().string(
                        containsString(
                                "name=\"category\"")));
    }

    @Test
    @DisplayName("should render item detail HTML"
            + " without template errors")
    void shouldRenderItemDetailHtml() throws Exception {
        String itemId = getFirstItemId();
        mockMvc.perform(get("/items/" + itemId)
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("item-row")))
                .andExpect(content().string(
                        containsString("selected")));
    }

    @Test
    @DisplayName("should render items with category"
            + " filter without template errors")
    void shouldRenderWithCategoryFilter()
            throws Exception {
        mockMvc.perform(get("/items")
                        .param("category", "HVAC")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("All Categories")));
    }

    @Test
    @DisplayName("should render items with search"
            + " without template errors")
    void shouldRenderWithSearch() throws Exception {
        mockMvc.perform(get("/items")
                        .param("q", "nonexistent-xyz")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "No items found")));
    }

    @Test
    @DisplayName("should render items with search"
            + " and category without template errors")
    void shouldRenderWithSearchAndCategory()
            throws Exception {
        mockMvc.perform(get("/items")
                        .param("q", "test")
                        .param("category", "HVAC")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("All Categories")));
    }

    @Test
    @DisplayName("should render item detail with"
            + " service records without template errors")
    void shouldRenderDetailWithRecords()
            throws Exception {
        String itemId = createItemWithSchedule();
        String vendorId = getFirstVendorId();
        mockMvc.perform(post("/items/" + itemId
                        + "/service-records")
                .param("summary", "Template test svc")
                .param("serviceDate",
                        LocalDate.now().toString())
                .param("cost", "50.00")
                .param("vendorId", vendorId)
                .with(user("dev").roles("USER"))
                .with(csrf()));
        mockMvc.perform(get("/items/" + itemId)
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString(
                                "Service History")))
                .andExpect(content().string(
                        containsString(
                                "Template test svc")))
                .andExpect(content().string(
                        containsString(
                                "Active Schedules")));
    }

    @Test
    @DisplayName("should sort items by name ascending"
            + " by default")
    void shouldSortByNameAscByDefault() throws Exception {
        mockMvc.perform(get("/items")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "itemPage"));
    }

    @Test
    @DisplayName("should sort items by name descending")
    void shouldSortByNameDesc() throws Exception {
        mockMvc.perform(get("/items")
                        .param("sort", "name")
                        .param("dir", "desc")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "itemPage"));
    }

    @Test
    @DisplayName("should sort items by location")
    void shouldSortByLocation() throws Exception {
        mockMvc.perform(get("/items")
                        .param("sort", "location")
                        .param("dir", "asc")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "itemPage"));
    }

    @Test
    @DisplayName("should sort items by category")
    void shouldSortByCategory() throws Exception {
        mockMvc.perform(get("/items")
                        .param("sort", "category")
                        .param("dir", "asc")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "itemPage"));
    }

    @Test
    @DisplayName("should sort items by manufacturer")
    void shouldSortByManufacturer() throws Exception {
        mockMvc.perform(get("/items")
                        .param("sort", "manufacturer")
                        .param("dir", "desc")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "itemPage"));
    }

    @Test
    @DisplayName("should fall back to name sort"
            + " for invalid field")
    void shouldFallBackWhenInvalidSort()
            throws Exception {
        mockMvc.perform(get("/items")
                        .param("sort", "invalid")
                        .param("dir", "asc")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "itemPage"));
    }

    @Test
    @DisplayName("should preserve sort with search")
    void shouldPreserveSortWithSearch() throws Exception {
        mockMvc.perform(get("/items")
                        .param("q", "Furnace")
                        .param("sort", "location")
                        .param("dir", "desc")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "itemPage"))
                .andExpect(model().attribute("q",
                        "Furnace"));
    }

    @Test
    @DisplayName("should preserve sort with category"
            + " filter")
    void shouldPreserveSortWithCategory()
            throws Exception {
        mockMvc.perform(get("/items")
                        .param("category", "HVAC")
                        .param("sort", "manufacturer")
                        .param("dir", "asc")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(
                        "itemPage"));
    }

    @Test
    @DisplayName("should render sort arrows in headers")
    void shouldRenderSortArrows() throws Exception {
        MvcResult result = mockMvc.perform(get("/items")
                        .param("sort", "name")
                        .param("dir", "asc")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(html.contains("sort-arrow"),
                "should render sort arrow indicator");
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

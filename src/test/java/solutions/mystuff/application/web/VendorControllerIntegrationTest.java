package solutions.mystuff.application.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure
        .AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
        .request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet
        .request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet
        .result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions
        .assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Vendor Controller Integration")
class VendorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("should show vendors page")
    void shouldShowVendorsPage() throws Exception {
        mockMvc.perform(get("/vendors")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model()
                        .attributeExists("vendors"));
    }

    @Test
    @DisplayName("should add vendor")
    void shouldAddVendor() throws Exception {
        mockMvc.perform(post("/vendors")
                        .param("name", "Integration Corp")
                        .param("phone", "555-0001")
                        .param("email", "test@int.com")
                        .param("city", "TestCity")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vendors"));
    }

    @Test
    @DisplayName("should reject blank name on add")
    void shouldRejectBlankName() throws Exception {
        mockMvc.perform(post("/vendors")
                        .param("name", "  ")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vendors"));
    }

    @Test
    @DisplayName("should edit vendor")
    void shouldEditVendor() throws Exception {
        mockMvc.perform(post("/vendors")
                .param("name", "EditMe Corp")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        String vendorId = findVendorId("EditMe Corp");

        mockMvc.perform(put("/vendors/" + vendorId)
                        .param("name", "Edited Corp")
                        .param("phone", "555-9999")
                        .param("website",
                                "https://edited.com")
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vendors"));
    }

    @Test
    @DisplayName("should delete vendor")
    void shouldDeleteVendor() throws Exception {
        mockMvc.perform(post("/vendors")
                .param("name", "DeleteMe Corp")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        String vendorId =
                findVendorId("DeleteMe Corp");

        mockMvc.perform(delete("/vendors/" + vendorId)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vendors"));
    }

    @Test
    @DisplayName("should export all vendors")
    void shouldExportAll() throws Exception {
        mockMvc.perform(post("/vendors")
                .param("name", "ExportTest Corp")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        mockMvc.perform(get("/vendors/export")
                        .with(user("dev").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Type",
                        "text/vcard; charset=utf-8"))
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename="
                                + "\"vendors.vcf\""));
    }

    @Test
    @DisplayName("should import vendors from vcf")
    void shouldImportVendors() throws Exception {
        String vcf = "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "FN:Imported Corp\r\n"
                + "TEL;TYPE=work:555-0002\r\n"
                + "END:VCARD\r\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "contacts.vcf",
                "text/vcard",
                vcf.getBytes());
        mockMvc.perform(multipart("/vendors/import")
                        .file(file)
                        .with(user("dev").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vendors"));
    }

    @Test
    @DisplayName("should render data-toggle-form for edit")
    void shouldRenderEditDataAttribute()
            throws Exception {
        mockMvc.perform(post("/vendors")
                .param("name", "DataAttr Corp")
                .with(user("dev").roles("USER"))
                .with(csrf()));

        MvcResult result = mockMvc.perform(
                        get("/vendors")
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(
                html.contains("data-toggle-form=\"edit-"),
                "should have data-toggle-form for edit");
        assertTrue(html.contains("/js/app.js"),
                "should include external app.js");
        assertTrue(!html.contains("onclick="),
                "should have no inline onclick");
    }

    @Test
    @DisplayName("should render cancel buttons on forms")
    void shouldRenderCancelButtons() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/vendors")
                                .with(user("dev")
                                        .roles("USER")))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        assertTrue(html.contains("btn-cancel"),
                "should have btn-cancel class");
        assertTrue(html.contains(
                "data-toggle-form=\"add-vendor-form\""),
                "cancel for add vendor form");
        assertTrue(html.contains(
                "data-toggle-form=\"import-vendor-form\""),
                "cancel for import vendor form");
    }

    private String findVendorId(String name)
            throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/vendors")
                                .with(user("dev")
                                        .roles("USER")))
                .andReturn();
        String html = result.getResponse()
                .getContentAsString();
        int nameIdx = html.indexOf(name);
        assertTrue(nameIdx > 0,
                "Vendor not found: " + name);
        String marker = "name=\"_method\" value=\"PUT\"";
        int putIdx = html.indexOf(marker, nameIdx);
        if (putIdx < 0) {
            putIdx = html.lastIndexOf(marker, nameIdx);
        }
        String actionMarker = "/vendors/";
        int actionIdx = html.lastIndexOf(
                actionMarker, putIdx);
        int idStart = actionIdx + actionMarker.length();
        int idEnd = html.indexOf("\"", idStart);
        return html.substring(idStart, idEnd);
    }
}

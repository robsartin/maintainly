package solutions.mystuff.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import solutions.mystuff.domain.model.NotFoundException;
import solutions.mystuff.domain.model.UuidV7;
import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.model.VendorAltPhone;
import solutions.mystuff.domain.port.out.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("VendorImportExportService")
class VendorImportExportServiceTest {

    private VendorRepository repository;
    private VendorImportExportService service;
    private UUID orgId;

    @BeforeEach
    void setUp() {
        repository = mock(VendorRepository.class);
        service = new VendorImportExportService(
                repository);
        orgId = UuidV7.generate();
        when(repository.save(any(Vendor.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("should export single vendor")
    void shouldExportSingle() {
        Vendor v = vendor("Acme");
        UUID vendorId = UuidV7.generate();
        when(repository.findByIdAndOrganizationId(
                vendorId, orgId))
                .thenReturn(Optional.of(v));
        String result = service.exportVendor(
                orgId, vendorId);
        assertTrue(result.contains("FN:Acme"));
    }

    @Test
    @DisplayName("should throw when vendor not found")
    void shouldThrowWhenNotFound() {
        UUID vendorId = UuidV7.generate();
        when(repository.findByIdAndOrganizationId(
                vendorId, orgId))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.exportVendor(
                        orgId, vendorId));
    }

    @Test
    @DisplayName("should export all vendors")
    void shouldExportAll() {
        when(repository.findByOrganizationId(orgId))
                .thenReturn(List.of(
                        vendor("A"), vendor("B")));
        String result =
                service.exportAllVendors(orgId);
        assertTrue(result.contains("FN:A"));
        assertTrue(result.contains("FN:B"));
    }

    @Test
    @DisplayName("should import vendor with all fields")
    void shouldImportAll() {
        String vcf = "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "FN:Test Vendor\r\n"
                + "TEL;TYPE=work:555-0100\r\n"
                + "EMAIL:test@example.com\r\n"
                + "ADR;TYPE=work:;;123 Main"
                + ";Springfield;IL;62701;US\r\n"
                + "URL:https://test.com\r\n"
                + "NOTE:Good service\r\n"
                + "END:VCARD\r\n";
        List<Vendor> result =
                service.importVendors(orgId, vcf);
        assertEquals(1, result.size());
        Vendor v = result.get(0);
        assertEquals("Test Vendor", v.getName());
        assertEquals("555-0100", v.getPhone());
        assertEquals("test@example.com", v.getEmail());
        assertEquals("123 Main", v.getAddressLine1());
        assertEquals("Springfield", v.getCity());
        assertEquals("IL", v.getStateProvince());
        assertEquals("62701", v.getPostalCode());
        assertEquals("US", v.getCountry());
        assertEquals("https://test.com",
                v.getWebsite());
        assertEquals("Good service", v.getNotes());
        assertEquals(orgId, v.getOrganizationId());
    }

    @Test
    @DisplayName("should import alt phones")
    void shouldImportAltPhones() {
        String vcf = "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "FN:Test\r\n"
                + "TEL;TYPE=work:555-0001\r\n"
                + "TEL;TYPE=mobile:555-0002\r\n"
                + "END:VCARD\r\n";
        List<Vendor> result =
                service.importVendors(orgId, vcf);
        Vendor v = result.get(0);
        assertEquals(1, v.getAltPhones().size());
        assertEquals("555-0002",
                v.getAltPhones().get(0).getPhone());
        assertEquals("mobile",
                v.getAltPhones().get(0).getLabel());
        assertNotNull(
                v.getAltPhones().get(0).getVendor());
    }

    @Test
    @DisplayName("should sanitize imported fields")
    void shouldSanitizeFields() {
        String vcf = "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "FN:Evil\tVendor\r\n"
                + "END:VCARD\r\n";
        List<Vendor> result =
                service.importVendors(orgId, vcf);
        assertFalse(
                result.get(0).getName().contains("\t"));
    }

    @Test
    @DisplayName("should truncate long name")
    void shouldTruncateName() {
        String longName = "A".repeat(300);
        String vcf = "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "FN:" + longName + "\r\n"
                + "END:VCARD\r\n";
        List<Vendor> result =
                service.importVendors(orgId, vcf);
        assertEquals(200,
                result.get(0).getName().length());
    }

    @Test
    @DisplayName("should truncate long notes")
    void shouldTruncateNotes() {
        String longNotes = "N".repeat(2500);
        String vcf = "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "FN:Test\r\n"
                + "NOTE:" + longNotes + "\r\n"
                + "END:VCARD\r\n";
        List<Vendor> result =
                service.importVendors(orgId, vcf);
        assertEquals(2000,
                result.get(0).getNotes().length());
    }

    @Test
    @DisplayName("should import multiple vendors")
    void shouldImportMultiple() {
        String vcf = "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "FN:Alpha\r\n"
                + "END:VCARD\r\n"
                + "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "FN:Beta\r\n"
                + "END:VCARD\r\n";
        List<Vendor> result =
                service.importVendors(orgId, vcf);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("should throw on empty content")
    void shouldThrowOnEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> service.importVendors(orgId, ""));
    }

    @Test
    @DisplayName("should throw when no contacts found")
    void shouldThrowWhenNoContacts() {
        String vcf = "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "TEL:555-0100\r\n"
                + "END:VCARD\r\n";
        assertThrows(IllegalArgumentException.class,
                () -> service.importVendors(
                        orgId, vcf));
    }

    private Vendor vendor(String name) {
        Vendor v = new Vendor();
        v.setName(name);
        v.setAltPhones(new ArrayList<>());
        return v;
    }
}

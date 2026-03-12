package solutions.mystuff.domain.service;

import java.util.ArrayList;
import java.util.List;

import solutions.mystuff.domain.model.Vendor;
import solutions.mystuff.domain.model.VendorAltPhone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("VCardSerializer")
class VCardSerializerTest {

    @Test
    @DisplayName("should serialize minimal vendor")
    void shouldSerializeMinimal() {
        Vendor v = vendor("Acme Corp");
        String vcard = VCardSerializer.serialize(v);
        assertTrue(vcard.startsWith(
                "BEGIN:VCARD\r\n"));
        assertTrue(vcard.contains(
                "VERSION:4.0\r\n"));
        assertTrue(vcard.contains(
                "FN:Acme Corp\r\n"));
        assertTrue(vcard.endsWith(
                "END:VCARD\r\n"));
    }

    @Test
    @DisplayName("should serialize phone")
    void shouldSerializePhone() {
        Vendor v = vendor("Test");
        v.setPhone("555-0100");
        String vcard = VCardSerializer.serialize(v);
        assertTrue(vcard.contains(
                "TEL;TYPE=work:555-0100\r\n"));
    }

    @Test
    @DisplayName("should serialize email")
    void shouldSerializeEmail() {
        Vendor v = vendor("Test");
        v.setEmail("info@test.com");
        String vcard = VCardSerializer.serialize(v);
        assertTrue(vcard.contains(
                "EMAIL:info@test.com\r\n"));
    }

    @Test
    @DisplayName("should serialize full address")
    void shouldSerializeAddress() {
        Vendor v = vendor("Test");
        v.setAddressLine1("123 Main St");
        v.setAddressLine2("Suite 100");
        v.setCity("Springfield");
        v.setStateProvince("IL");
        v.setPostalCode("62701");
        v.setCountry("US");
        String vcard = VCardSerializer.serialize(v);
        assertTrue(vcard.contains(
                "ADR;TYPE=work:;Suite 100;123 Main St"
                        + ";Springfield;IL;62701;US\r\n"));
    }

    @Test
    @DisplayName("should serialize partial address")
    void shouldSerializePartialAddress() {
        Vendor v = vendor("Test");
        v.setCity("Springfield");
        String vcard = VCardSerializer.serialize(v);
        assertTrue(vcard.contains(
                "ADR;TYPE=work:;;;"
                        + "Springfield;;;\r\n"));
    }

    @Test
    @DisplayName("should serialize website")
    void shouldSerializeWebsite() {
        Vendor v = vendor("Test");
        v.setWebsite("https://test.com");
        String vcard = VCardSerializer.serialize(v);
        assertTrue(vcard.contains(
                "URL:https://test.com\r\n"));
    }

    @Test
    @DisplayName("should serialize notes")
    void shouldSerializeNotes() {
        Vendor v = vendor("Test");
        v.setNotes("Good vendor");
        String vcard = VCardSerializer.serialize(v);
        assertTrue(vcard.contains(
                "NOTE:Good vendor\r\n"));
    }

    @Test
    @DisplayName("should serialize alt phones")
    void shouldSerializeAltPhones() {
        Vendor v = vendor("Test");
        v.setAltPhones(new ArrayList<>());
        VendorAltPhone alt = new VendorAltPhone();
        alt.setPhone("555-0200");
        alt.setLabel("mobile");
        alt.setVendor(v);
        v.getAltPhones().add(alt);
        String vcard = VCardSerializer.serialize(v);
        assertTrue(vcard.contains(
                "TEL;TYPE=mobile:555-0200\r\n"));
    }

    @Test
    @DisplayName("should escape special characters")
    void shouldEscapeSpecialChars() {
        Vendor v = vendor("Acme; Inc\\");
        v.setNotes("Line1\nLine2");
        String vcard = VCardSerializer.serialize(v);
        assertTrue(vcard.contains(
                "FN:Acme\\; Inc\\\\\r\n"));
        assertTrue(vcard.contains(
                "NOTE:Line1\\nLine2\r\n"));
    }

    @Test
    @DisplayName("should skip null fields")
    void shouldSkipNullFields() {
        Vendor v = vendor("Test");
        String vcard = VCardSerializer.serialize(v);
        assertFalse(vcard.contains("TEL"));
        assertFalse(vcard.contains("EMAIL"));
        assertFalse(vcard.contains("ADR"));
        assertFalse(vcard.contains("URL"));
        assertFalse(vcard.contains("NOTE"));
    }

    @Test
    @DisplayName("should serialize multiple vendors")
    void shouldSerializeMultiple() {
        Vendor v1 = vendor("Alpha");
        Vendor v2 = vendor("Beta");
        String vcards =
                VCardSerializer.serializeAll(
                        List.of(v1, v2));
        assertEquals(2, countOccurrences(
                vcards, "BEGIN:VCARD"));
        assertEquals(2, countOccurrences(
                vcards, "END:VCARD"));
    }

    private Vendor vendor(String name) {
        Vendor v = new Vendor();
        v.setName(name);
        v.setAltPhones(new ArrayList<>());
        return v;
    }

    private int countOccurrences(
            String text, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }
}

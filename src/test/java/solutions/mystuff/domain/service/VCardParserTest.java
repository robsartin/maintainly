package solutions.mystuff.domain.service;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("VCardParser")
@SuppressWarnings("unchecked")
class VCardParserTest {

    @Test
    @DisplayName("should parse minimal vCard")
    void shouldParseMinimal() {
        String vcf = vcard("FN:Acme Corp\r\n");
        List<Map<String, Object>> result =
                VCardParser.parse(vcf);
        assertEquals(1, result.size());
        assertEquals("Acme Corp",
                result.get(0).get("name"));
    }

    @Test
    @DisplayName("should parse phone with type")
    void shouldParsePhone() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "TEL;TYPE=work:555-0100\r\n");
        Map<String, Object> card =
                VCardParser.parse(vcf).get(0);
        assertEquals("555-0100", card.get("phone"));
    }

    @Test
    @DisplayName("should parse email")
    void shouldParseEmail() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "EMAIL:info@test.com\r\n");
        Map<String, Object> card =
                VCardParser.parse(vcf).get(0);
        assertEquals("info@test.com",
                card.get("email"));
    }

    @Test
    @DisplayName("should parse full address")
    void shouldParseAddress() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "ADR;TYPE=work:;Suite 100"
                        + ";123 Main;Springfield"
                        + ";IL;62701;US\r\n");
        Map<String, Object> card =
                VCardParser.parse(vcf).get(0);
        assertEquals("123 Main",
                card.get("addressLine1"));
        assertEquals("Suite 100",
                card.get("addressLine2"));
        assertEquals("Springfield",
                card.get("city"));
        assertEquals("IL",
                card.get("stateProvince"));
        assertEquals("62701",
                card.get("postalCode"));
        assertEquals("US", card.get("country"));
    }

    @Test
    @DisplayName("should parse website")
    void shouldParseWebsite() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "URL:https://test.com\r\n");
        Map<String, Object> card =
                VCardParser.parse(vcf).get(0);
        assertEquals("https://test.com",
                card.get("website"));
    }

    @Test
    @DisplayName("should parse notes")
    void shouldParseNotes() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "NOTE:Good vendor\r\n");
        Map<String, Object> card =
                VCardParser.parse(vcf).get(0);
        assertEquals("Good vendor",
                card.get("notes"));
    }

    @Test
    @DisplayName("should parse multiple TEL as alt phones")
    void shouldParseMultipleTel() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "TEL;TYPE=work:555-0001\r\n"
                        + "TEL;TYPE=mobile:555-0002\r\n"
                        + "TEL;TYPE=fax:555-0003\r\n");
        Map<String, Object> card =
                VCardParser.parse(vcf).get(0);
        assertEquals("555-0001", card.get("phone"));
        List<Map<String, String>> alts =
                (List<Map<String, String>>)
                        card.get("altPhones");
        assertNotNull(alts);
        assertEquals(2, alts.size());
        assertEquals("555-0002",
                alts.get(0).get("phone"));
        assertEquals("mobile",
                alts.get(0).get("label"));
    }

    @Test
    @DisplayName("should unescape special characters")
    void shouldUnescapeChars() {
        String vcf = vcard(
                "FN:Acme\\; Inc\\\\\r\n"
                        + "NOTE:Line1\\nLine2\r\n");
        Map<String, Object> card =
                VCardParser.parse(vcf).get(0);
        assertEquals("Acme; Inc\\",
                card.get("name"));
        assertEquals("Line1\nLine2",
                card.get("notes"));
    }

    @Test
    @DisplayName("should handle line unfolding")
    void shouldUnfoldLines() {
        String vcf = vcard(
                "FN:Very Long \r\n"
                        + " Name Here\r\n");
        Map<String, Object> card =
                VCardParser.parse(vcf).get(0);
        assertEquals("Very Long Name Here",
                card.get("name"));
    }

    @Test
    @DisplayName("should skip vCards without FN")
    void shouldSkipWithoutFn() {
        String vcf = "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "TEL:555-0100\r\n"
                + "END:VCARD\r\n";
        List<Map<String, Object>> result =
                VCardParser.parse(vcf);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should parse multiple vCards")
    void shouldParseMultiple() {
        String vcf = vcard("FN:Alpha\r\n")
                + vcard("FN:Beta\r\n");
        List<Map<String, Object>> result =
                VCardParser.parse(vcf);
        assertEquals(2, result.size());
        assertEquals("Alpha",
                result.get(0).get("name"));
        assertEquals("Beta",
                result.get(1).get("name"));
    }

    @Test
    @DisplayName("should handle LF line endings")
    void shouldHandleLfEndings() {
        String vcf = "BEGIN:VCARD\n"
                + "VERSION:4.0\n"
                + "FN:Test\n"
                + "END:VCARD\n";
        List<Map<String, Object>> result =
                VCardParser.parse(vcf);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("should be case insensitive")
    void shouldBeCaseInsensitive() {
        String vcf = "begin:vcard\r\n"
                + "version:4.0\r\n"
                + "fn:Test\r\n"
                + "end:vcard\r\n";
        List<Map<String, Object>> result =
                VCardParser.parse(vcf);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("should enforce max 100 vCards")
    void shouldEnforceMax() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append(vcard("FN:Vendor " + i + "\r\n"));
        }
        assertThrows(IllegalArgumentException.class,
                () -> VCardParser.parse(sb.toString()));
    }

    @Test
    @DisplayName("should parse TEL without type")
    void shouldParseTelWithoutType() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "TEL:555-0001\r\n");
        Map<String, Object> card =
                VCardParser.parse(vcf).get(0);
        assertEquals("555-0001", card.get("phone"));
    }

    @Test
    @DisplayName("should handle empty input")
    void shouldHandleEmptyInput() {
        List<Map<String, Object>> result =
                VCardParser.parse("");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should ignore unknown properties")
    void shouldIgnoreUnknownProperties() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "X-CUSTOM:value\r\n"
                        + "BDAY:19700101\r\n");
        Map<String, Object> card =
                VCardParser.parse(vcf).get(0);
        assertEquals("Test", card.get("name"));
        assertNull(card.get("X-CUSTOM"));
    }

    private String vcard(String properties) {
        return "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + properties
                + "END:VCARD\r\n";
    }
}

package solutions.mystuff.domain.service;

import java.util.List;

import solutions.mystuff.domain.model.ParsedAltPhone;
import solutions.mystuff.domain.model.ParsedVCard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("VCardParser")
class VCardParserTest {

    @Test
    @DisplayName("should parse minimal vCard")
    void shouldParseMinimal() {
        String vcf = vcard("FN:Acme Corp\r\n");
        List<ParsedVCard> result =
                VCardParser.parse(vcf);
        assertEquals(1, result.size());
        assertEquals("Acme Corp",
                result.get(0).name());
    }

    @Test
    @DisplayName("should parse phone with type")
    void shouldParsePhone() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "TEL;TYPE=work:555-0100\r\n");
        ParsedVCard card =
                VCardParser.parse(vcf).get(0);
        assertEquals("555-0100", card.phone());
    }

    @Test
    @DisplayName("should parse email")
    void shouldParseEmail() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "EMAIL:info@test.com\r\n");
        ParsedVCard card =
                VCardParser.parse(vcf).get(0);
        assertEquals("info@test.com", card.email());
    }

    @Test
    @DisplayName("should parse full address")
    void shouldParseAddress() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "ADR;TYPE=work:;Suite 100"
                        + ";123 Main;Springfield"
                        + ";IL;62701;US\r\n");
        ParsedVCard card =
                VCardParser.parse(vcf).get(0);
        assertEquals("123 Main",
                card.addressLine1());
        assertEquals("Suite 100",
                card.addressLine2());
        assertEquals("Springfield", card.city());
        assertEquals("IL", card.stateProvince());
        assertEquals("62701", card.postalCode());
        assertEquals("US", card.country());
    }

    @Test
    @DisplayName("should parse website")
    void shouldParseWebsite() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "URL:https://test.com\r\n");
        ParsedVCard card =
                VCardParser.parse(vcf).get(0);
        assertEquals("https://test.com",
                card.website());
    }

    @Test
    @DisplayName("should parse notes")
    void shouldParseNotes() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "NOTE:Good vendor\r\n");
        ParsedVCard card =
                VCardParser.parse(vcf).get(0);
        assertEquals("Good vendor", card.notes());
    }

    @Test
    @DisplayName("should parse multiple TEL as alt phones")
    void shouldParseMultipleTel() {
        String vcf = vcard(
                "FN:Test\r\n"
                        + "TEL;TYPE=work:555-0001\r\n"
                        + "TEL;TYPE=mobile:555-0002\r\n"
                        + "TEL;TYPE=fax:555-0003\r\n");
        ParsedVCard card =
                VCardParser.parse(vcf).get(0);
        assertEquals("555-0001", card.phone());
        List<ParsedAltPhone> alts = card.altPhones();
        assertNotNull(alts);
        assertEquals(2, alts.size());
        assertEquals("555-0002",
                alts.get(0).phone());
        assertEquals("mobile",
                alts.get(0).label());
    }

    @Test
    @DisplayName("should unescape special characters")
    void shouldUnescapeChars() {
        String vcf = vcard(
                "FN:Acme\\; Inc\\\\\r\n"
                        + "NOTE:Line1\\nLine2\r\n");
        ParsedVCard card =
                VCardParser.parse(vcf).get(0);
        assertEquals("Acme; Inc\\", card.name());
        assertEquals("Line1\nLine2", card.notes());
    }

    @Test
    @DisplayName("should handle line unfolding")
    void shouldUnfoldLines() {
        String vcf = vcard(
                "FN:Very Long \r\n"
                        + " Name Here\r\n");
        ParsedVCard card =
                VCardParser.parse(vcf).get(0);
        assertEquals("Very Long Name Here",
                card.name());
    }

    @Test
    @DisplayName("should skip vCards without FN")
    void shouldSkipWithoutFn() {
        String vcf = "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + "TEL:555-0100\r\n"
                + "END:VCARD\r\n";
        List<ParsedVCard> result =
                VCardParser.parse(vcf);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should parse multiple vCards")
    void shouldParseMultiple() {
        String vcf = vcard("FN:Alpha\r\n")
                + vcard("FN:Beta\r\n");
        List<ParsedVCard> result =
                VCardParser.parse(vcf);
        assertEquals(2, result.size());
        assertEquals("Alpha", result.get(0).name());
        assertEquals("Beta", result.get(1).name());
    }

    @Test
    @DisplayName("should handle LF line endings")
    void shouldHandleLfEndings() {
        String vcf = "BEGIN:VCARD\n"
                + "VERSION:4.0\n"
                + "FN:Test\n"
                + "END:VCARD\n";
        List<ParsedVCard> result =
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
        List<ParsedVCard> result =
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
        ParsedVCard card =
                VCardParser.parse(vcf).get(0);
        assertEquals("555-0001", card.phone());
    }

    @Test
    @DisplayName("should handle empty input")
    void shouldHandleEmptyInput() {
        List<ParsedVCard> result =
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
        ParsedVCard card =
                VCardParser.parse(vcf).get(0);
        assertEquals("Test", card.name());
    }

    private String vcard(String properties) {
        return "BEGIN:VCARD\r\n"
                + "VERSION:4.0\r\n"
                + properties
                + "END:VCARD\r\n";
    }
}

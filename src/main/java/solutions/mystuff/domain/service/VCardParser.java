package solutions.mystuff.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Parses vCard 4.0 (RFC 6350) content into structured maps.
 *
 * <p>Handles line unfolding, property parameter parsing, value
 * unescaping, case-insensitive property names, and both CRLF
 * and LF line endings. Skips vCards without an FN property.
 *
 * @see VCardSerializer
 */
public final class VCardParser {

    private static final int MAX_VCARDS = 100;

    private VCardParser() {
    }

    /**
     * Parses vCard content into a list of property maps.
     *
     * @param vcfContent the raw vCard file content
     * @return list of maps with vendor field keys
     * @throws IllegalArgumentException if more than 100 vCards
     */
    public static List<Map<String, Object>> parse(
            String vcfContent) {
        if (vcfContent == null || vcfContent.isBlank()) {
            return List.of();
        }
        String[] lines = unfold(vcfContent);
        List<Map<String, Object>> results =
                new ArrayList<>();
        List<String> currentLines = null;
        for (String line : lines) {
            currentLines = processLine(
                    line, currentLines, results);
        }
        return results;
    }

    private static String[] unfold(String content) {
        String normalized = content
                .replace("\r\n", "\n")
                .replace("\r", "\n");
        return normalized
                .replaceAll("\n[ \t]", "")
                .split("\n");
    }

    private static List<String> processLine(
            String line, List<String> currentLines,
            List<Map<String, Object>> results) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return currentLines;
        }
        String upper =
                trimmed.toUpperCase(Locale.ROOT);
        if (upper.equals("BEGIN:VCARD")) {
            return new ArrayList<>();
        }
        if (upper.equals("END:VCARD")) {
            return finishCard(currentLines, results);
        }
        if (currentLines != null) {
            currentLines.add(trimmed);
        }
        return currentLines;
    }

    private static List<String> finishCard(
            List<String> currentLines,
            List<Map<String, Object>> results) {
        if (currentLines == null) {
            return null;
        }
        Map<String, Object> card =
                parseCard(currentLines);
        if (card.containsKey("name")) {
            results.add(card);
            if (results.size() > MAX_VCARDS) {
                throw new IllegalArgumentException(
                        "Import limited to "
                                + MAX_VCARDS
                                + " contacts");
            }
        }
        return null;
    }

    private static Map<String, Object> parseCard(
            List<String> lines) {
        Map<String, Object> card = new HashMap<>();
        List<Map<String, String>> altPhones =
                new ArrayList<>();
        boolean firstTel = true;
        for (String line : lines) {
            firstTel = parseLine(
                    line, card, altPhones, firstTel);
        }
        if (!altPhones.isEmpty()) {
            card.put("altPhones", altPhones);
        }
        return card;
    }

    private static boolean parseLine(
            String line, Map<String, Object> card,
            List<Map<String, String>> altPhones,
            boolean firstTel) {
        int colonIdx = line.indexOf(':');
        if (colonIdx < 0) {
            return firstTel;
        }
        String propPart =
                line.substring(0, colonIdx);
        String value =
                line.substring(colonIdx + 1);
        String propName = extractPropName(propPart);
        return applyProperty(
                propName, propPart, value,
                card, altPhones, firstTel);
    }

    private static boolean applyProperty(
            String propName, String propPart,
            String value, Map<String, Object> card,
            List<Map<String, String>> altPhones,
            boolean firstTel) {
        switch (propName) {
            case "FN":
                card.put("name", unescape(value));
                break;
            case "TEL":
                handleTel(card, altPhones,
                        propPart, value, firstTel);
                return false;
            case "EMAIL":
                card.put("email", unescape(value));
                break;
            case "ADR":
                parseAddress(card, value);
                break;
            case "URL":
                card.put("website", unescape(value));
                break;
            case "NOTE":
                card.put("notes", unescape(value));
                break;
            default:
                break;
        }
        return firstTel;
    }

    private static String extractPropName(
            String propPart) {
        int semiIdx = propPart.indexOf(';');
        String name = semiIdx >= 0
                ? propPart.substring(0, semiIdx)
                : propPart;
        return name.toUpperCase(Locale.ROOT);
    }

    private static void handleTel(
            Map<String, Object> card,
            List<Map<String, String>> altPhones,
            String propPart, String value,
            boolean firstTel) {
        String phone = unescape(value);
        String label = extractType(propPart);
        if (firstTel) {
            card.put("phone", phone);
        } else {
            Map<String, String> alt = new HashMap<>();
            alt.put("phone", phone);
            alt.put("label",
                    label != null ? label : "work");
            altPhones.add(alt);
        }
    }

    private static String extractType(
            String propPart) {
        String upper =
                propPart.toUpperCase(Locale.ROOT);
        int typeIdx = upper.indexOf("TYPE=");
        if (typeIdx < 0) {
            return null;
        }
        String after = propPart.substring(
                typeIdx + "TYPE=".length());
        int end = after.indexOf(';');
        String typeVal = end >= 0
                ? after.substring(0, end) : after;
        return typeVal.replace("\"", "")
                .split(",")[0]
                .toLowerCase(Locale.ROOT);
    }

    private static void parseAddress(
            Map<String, Object> card, String value) {
        String[] parts = splitAdr(value);
        setIfPresent(card, "addressLine2", parts, 1);
        setIfPresent(card, "addressLine1", parts, 2);
        setIfPresent(card, "city", parts, 3);
        setIfPresent(card, "stateProvince", parts, 4);
        setIfPresent(card, "postalCode", parts, 5);
        setIfPresent(card, "country", parts, 6);
    }

    private static String[] splitAdr(String value) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                current.append(c);
                current.append(value.charAt(++i));
            } else if (c == ';') {
                parts.add(unescape(
                        current.toString()));
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        parts.add(unescape(current.toString()));
        return parts.toArray(new String[0]);
    }

    private static void setIfPresent(
            Map<String, Object> card, String key,
            String[] parts, int index) {
        if (index < parts.length
                && !parts[index].isEmpty()) {
            card.put(key, parts[index]);
        }
    }

    static String unescape(String value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                sb.append(unescapeChar(
                        value.charAt(++i)));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String unescapeChar(char next) {
        switch (next) {
            case 'n':
            case 'N':
                return "\n";
            case ';':
                return ";";
            case '\\':
                return "\\";
            case ',':
                return ",";
            default:
                return "\\" + next;
        }
    }
}

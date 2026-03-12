package solutions.mystuff.domain.service;

/**
 * Column length constants for {@link solutions.mystuff.domain.model.Vendor}
 * and related entities.
 *
 * <p>Centralizes maximum field lengths so that validation and
 * truncation logic stays consistent across services.
 *
 * @see VendorManagementService
 * @see VendorImportExportService
 */
public final class VendorFieldLimits {

    static final int MAX_NAME = 200;
    static final int MAX_PHONE = 50;
    static final int MAX_EMAIL = 320;
    static final int MAX_ADDR = 200;
    static final int MAX_CITY = 100;
    static final int MAX_STATE = 100;
    static final int MAX_POSTAL = 30;
    static final int MAX_COUNTRY = 100;
    static final int MAX_URL = 2000;
    static final int MAX_NOTES = 2000;
    static final int MAX_LABEL = 50;

    private VendorFieldLimits() {
    }

    /** Truncates a value to the given max length, or returns null. */
    static String truncate(
            String value, int maxLength) {
        if (value == null) {
            return null;
        }
        if (value.length() > maxLength) {
            return value.substring(0, maxLength);
        }
        return value;
    }
}

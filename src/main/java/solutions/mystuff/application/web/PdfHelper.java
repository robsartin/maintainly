package solutions.mystuff.application.web;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * Shared PDF helper methods for table building, formatting,
 * and org headers, used by all PDF report generators.
 */
final class PdfHelper {

    static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy");
    static final Font TITLE_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 16);
    static final Font SECTION_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 13);
    static final Font LABEL_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 10);
    static final Font BODY_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA, 10);
    static final java.awt.Color HEADER_BG =
            new java.awt.Color(220, 220, 220);
    /** Overdue row: muted red, dark in B&W. */
    static final java.awt.Color ROW_OVERDUE =
            new java.awt.Color(248, 200, 200);
    /** Due soon row: muted gold, medium in B&W. */
    static final java.awt.Color ROW_SOON =
            new java.awt.Color(255, 240, 190);
    /** On track row: muted green, lightest in B&W. */
    static final java.awt.Color ROW_OK =
            new java.awt.Color(235, 255, 235);

    private PdfHelper() {
    }

    static void addOrgHeader(
            Document doc, String orgName,
            String username) throws Exception {
        Paragraph org = new Paragraph(
                orgName, LABEL_FONT);
        org.setAlignment(Element.ALIGN_LEFT);
        doc.add(org);
        Paragraph user = new Paragraph(
                username, BODY_FONT);
        user.setAlignment(Element.ALIGN_LEFT);
        user.setSpacingAfter(8);
        doc.add(user);
    }

    static void addHeaderRow(
            PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(
                    new Phrase(h, LABEL_FONT));
            cell.setBackgroundColor(HEADER_BG);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    static void addCell(
            PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text, BODY_FONT));
        cell.setPadding(4);
        table.addCell(cell);
    }

    static void addCell(
            PdfPTable table, String text,
            java.awt.Color bg) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text, BODY_FONT));
        cell.setPadding(4);
        if (bg != null) {
            cell.setBackgroundColor(bg);
        }
        table.addCell(cell);
    }

    /** Returns the row color for a schedule's due date status. */
    static java.awt.Color rowColor(
            LocalDate nextDue,
            LocalDate today, LocalDate soon) {
        if (nextDue == null) {
            return null;
        }
        if (nextDue.isBefore(today)) {
            return ROW_OVERDUE;
        }
        if (nextDue.isBefore(soon)) {
            return ROW_SOON;
        }
        return ROW_OK;
    }

    static String fmtDate(LocalDate date) {
        return date != null ? date.format(FMT) : "";
    }

    static String safe(String val) {
        return val != null ? val : "";
    }
}

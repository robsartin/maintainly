package solutions.mystuff.application.web;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import solutions.mystuff.domain.model.ServiceSchedule;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Generates a PDF report of service schedules due soon
 * using OpenPDF.
 *
 * <div class="mermaid">
 * sequenceDiagram
 *     ReportController->>ServiceSummaryPdf: write(response, schedules, cutoff, ...)
 *     ServiceSummaryPdf->>Document: org header, cutoff title, schedule table
 *     ServiceSummaryPdf-->>Browser: PDF via HttpServletResponse
 * </div>
 *
 * @see ReportController
 */
final class ServiceSummaryPdf {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final Font TITLE_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 16);
    private static final Font HEADER_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 10);
    private static final Font BODY_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA, 10);
    private static final java.awt.Color HEADER_BG =
            new java.awt.Color(220, 220, 220);

    private ServiceSummaryPdf() {
    }

    /** Writes the service summary PDF to the HTTP response stream. */
    static void write(
            HttpServletResponse response,
            List<ServiceSchedule> schedules,
            LocalDate cutoff,
            String orgName,
            String username) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "inline; filename=service-due.pdf");
        Document doc = new Document(
                PageSize.LETTER.rotate());
        PdfWriter.getInstance(doc,
                response.getOutputStream());
        doc.open();
        addOrgHeader(doc, orgName, username);
        addTitle(doc, orgName, cutoff);
        if (schedules.isEmpty()) {
            doc.add(new Paragraph(
                    "No service due.", BODY_FONT));
        } else {
            doc.add(buildTable(schedules));
        }
        doc.close();
    }

    private static void addOrgHeader(
            Document doc, String orgName,
            String username) throws Exception {
        Paragraph org = new Paragraph(
                orgName, HEADER_FONT);
        org.setAlignment(Element.ALIGN_LEFT);
        doc.add(org);
        Paragraph user = new Paragraph(
                username, BODY_FONT);
        user.setAlignment(Element.ALIGN_LEFT);
        user.setSpacingAfter(8);
        doc.add(user);
    }

    private static void addTitle(
            Document doc, String orgName,
            LocalDate cutoff) throws Exception {
        Paragraph title = new Paragraph(
                orgName
                        + " — Service Due Through "
                        + cutoff.format(FMT),
                TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4);
        doc.add(title);
        Paragraph gen = new Paragraph(
                "Generated "
                        + LocalDate.now().format(FMT),
                BODY_FONT);
        gen.setAlignment(Element.ALIGN_RIGHT);
        gen.setSpacingAfter(12);
        doc.add(gen);
    }

    private static PdfPTable buildTable(
            List<ServiceSchedule> schedules)
            throws Exception {
        PdfPTable table = new PdfPTable(
                new float[]{3, 2, 2, 2, 2, 2, 2});
        table.setWidthPercentage(100);
        addHeaderRow(table, "Item", "Location",
                "Service Type", "Vendor",
                "Next Due", "Frequency",
                "Last Completed");
        for (ServiceSchedule s : schedules) {
            addRow(table, s);
        }
        return table;
    }

    private static void addRow(
            PdfPTable table, ServiceSchedule s) {
        addCell(table, s.getItem().getName());
        addCell(table, safe(
                s.getItem().getLocation()));
        addCell(table, s.getServiceType());
        addCell(table,
                s.getPreferredVendor() != null
                        ? s.getPreferredVendor().getName()
                        : "");
        addCell(table, fmtDate(s.getNextDueDate()));
        addCell(table,
                "Every " + s.getFrequencyInterval()
                        + " " + s.getFrequencyUnit());
        addCell(table,
                fmtDate(s.getLastCompletedDate()));
    }

    private static void addHeaderRow(
            PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(
                    new Phrase(h, HEADER_FONT));
            cell.setBackgroundColor(HEADER_BG);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private static void addCell(
            PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text, BODY_FONT));
        cell.setPadding(4);
        table.addCell(cell);
    }

    private static String fmtDate(LocalDate date) {
        return date != null ? date.format(FMT) : "";
    }

    private static String safe(String val) {
        return val != null ? val : "";
    }
}

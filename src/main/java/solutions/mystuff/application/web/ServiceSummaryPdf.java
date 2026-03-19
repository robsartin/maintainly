package solutions.mystuff.application.web;

import java.time.LocalDate;
import java.util.List;

import solutions.mystuff.domain.model.ServiceSchedule;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Generates a PDF report of service schedules due soon
 * using OpenPDF. Rows are color-coded by urgency:
 * red (overdue), yellow (due soon), green (on track).
 *
 * @see ReportController
 * @see PdfHelper
 */
final class ServiceSummaryPdf {

    private ServiceSummaryPdf() {
    }

    /** Writes the service summary PDF to the HTTP response stream. */
    static void write(
            HttpServletResponse response,
            List<ServiceSchedule> schedules,
            LocalDate endDate,
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
        PdfHelper.addOrgHeader(doc, orgName, username);
        addTitle(doc, orgName, endDate);
        if (schedules.isEmpty()) {
            doc.add(new Paragraph(
                    "No service due.",
                    PdfHelper.BODY_FONT));
        } else {
            LocalDate today = LocalDate.now();
            LocalDate soon = today.plusWeeks(2);
            addLegend(doc);
            doc.add(buildTable(schedules, today, soon));
        }
        doc.close();
    }

    private static void addTitle(
            Document doc, String orgName,
            LocalDate endDate) throws Exception {
        Paragraph title = new Paragraph(
                orgName + " — Service Due Through "
                        + endDate.format(PdfHelper.FMT),
                PdfHelper.TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4);
        doc.add(title);
        Paragraph gen = new Paragraph(
                "Generated "
                        + LocalDate.now().format(
                                PdfHelper.FMT),
                PdfHelper.BODY_FONT);
        gen.setAlignment(Element.ALIGN_RIGHT);
        gen.setSpacingAfter(6);
        doc.add(gen);
    }

    private static void addLegend(Document doc)
            throws Exception {
        PdfPTable legend = new PdfPTable(6);
        legend.setWidthPercentage(50);
        legend.setHorizontalAlignment(
                Element.ALIGN_LEFT);
        addLegendEntry(legend, PdfHelper.ROW_OVERDUE,
                "Overdue");
        addLegendEntry(legend, PdfHelper.ROW_SOON,
                "Due within 2 weeks");
        addLegendEntry(legend, PdfHelper.ROW_OK,
                "On track");
        legend.setSpacingAfter(8);
        doc.add(legend);
    }

    private static void addLegendEntry(
            PdfPTable table, java.awt.Color color,
            String label) {
        PdfPCell swatch = new PdfPCell(new Phrase(" "));
        swatch.setBackgroundColor(color);
        swatch.setFixedHeight(12);
        swatch.setBorderWidth(0.5f);
        table.addCell(swatch);
        PdfPCell text = new PdfPCell(
                new Phrase(label, PdfHelper.BODY_FONT));
        text.setBorderWidth(0);
        text.setPaddingLeft(4);
        table.addCell(text);
    }

    private static PdfPTable buildTable(
            List<ServiceSchedule> schedules,
            LocalDate today, LocalDate soon)
            throws Exception {
        PdfPTable table = new PdfPTable(
                new float[]{3, 2, 2, 2, 2, 2, 2});
        table.setWidthPercentage(100);
        PdfHelper.addHeaderRow(table, "Item",
                "Location", "Service Type", "Vendor",
                "Next Due", "Frequency",
                "Last Completed");
        for (ServiceSchedule s : schedules) {
            addRow(table, s, today, soon);
        }
        return table;
    }

    private static void addRow(
            PdfPTable table, ServiceSchedule s,
            LocalDate today, LocalDate soon) {
        java.awt.Color bg = PdfHelper.rowColor(
                s.getNextDueDate(), today, soon);
        PdfHelper.addCell(table,
                s.getItem().getName(), bg);
        PdfHelper.addCell(table,
                PdfHelper.safe(
                        s.getItem().getLocation()), bg);
        PdfHelper.addCell(table,
                s.getServiceType(), bg);
        PdfHelper.addCell(table,
                s.getPreferredVendor() != null
                        ? s.getPreferredVendor().getName()
                        : "", bg);
        PdfHelper.addCell(table,
                PdfHelper.fmtDate(
                        s.getNextDueDate()), bg);
        PdfHelper.addCell(table,
                "Every " + s.getFrequencyInterval()
                        + " " + s.getFrequencyUnit(), bg);
        PdfHelper.addCell(table,
                PdfHelper.fmtDate(
                        s.getLastCompletedDate()), bg);
    }
}

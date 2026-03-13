package solutions.mystuff.application.web;

import java.time.LocalDate;
import java.util.List;

import solutions.mystuff.domain.model.ServiceSchedule;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Generates a PDF report of service schedules due soon
 * using OpenPDF.
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
        PdfHelper.addOrgHeader(doc, orgName, username);
        addTitle(doc, orgName, cutoff);
        if (schedules.isEmpty()) {
            doc.add(new Paragraph(
                    "No service due.",
                    PdfHelper.BODY_FONT));
        } else {
            doc.add(buildTable(schedules));
        }
        doc.close();
    }

    private static void addTitle(
            Document doc, String orgName,
            LocalDate cutoff) throws Exception {
        Paragraph title = new Paragraph(
                orgName
                        + " — Service Due Through "
                        + cutoff.format(PdfHelper.FMT),
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
        gen.setSpacingAfter(12);
        doc.add(gen);
    }

    private static PdfPTable buildTable(
            List<ServiceSchedule> schedules)
            throws Exception {
        PdfPTable table = new PdfPTable(
                new float[]{3, 2, 2, 2, 2, 2, 2});
        table.setWidthPercentage(100);
        PdfHelper.addHeaderRow(table, "Item",
                "Location", "Service Type", "Vendor",
                "Next Due", "Frequency",
                "Last Completed");
        for (ServiceSchedule s : schedules) {
            addRow(table, s);
        }
        return table;
    }

    private static void addRow(
            PdfPTable table, ServiceSchedule s) {
        PdfHelper.addCell(table,
                s.getItem().getName());
        PdfHelper.addCell(table,
                PdfHelper.safe(
                        s.getItem().getLocation()));
        PdfHelper.addCell(table, s.getServiceType());
        PdfHelper.addCell(table,
                s.getPreferredVendor() != null
                        ? s.getPreferredVendor().getName()
                        : "");
        PdfHelper.addCell(table,
                PdfHelper.fmtDate(s.getNextDueDate()));
        PdfHelper.addCell(table,
                "Every " + s.getFrequencyInterval()
                        + " " + s.getFrequencyUnit());
        PdfHelper.addCell(table,
                PdfHelper.fmtDate(
                        s.getLastCompletedDate()));
    }
}

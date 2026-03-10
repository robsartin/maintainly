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

final class DueNextMonthPdf {

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

    private DueNextMonthPdf() {
    }

    static void write(
            HttpServletResponse response,
            List<ServiceSchedule> schedules,
            LocalDate cutoff, String orgName)
            throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "inline; filename=due-next-month.pdf");
        Document doc = new Document(
                PageSize.LETTER.rotate());
        PdfWriter.getInstance(doc,
                response.getOutputStream());
        doc.open();
        addTitle(doc, orgName, cutoff);
        if (schedules.isEmpty()) {
            doc.add(new Paragraph(
                    "No service due.", BODY_FONT));
        } else {
            doc.add(buildTable(schedules));
        }
        doc.close();
    }

    private static void addTitle(
            Document doc, String orgName,
            LocalDate cutoff) throws Exception {
        Paragraph title = new Paragraph(
                orgName + " — Service Due Through "
                        + cutoff.format(FMT),
                TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(12);
        doc.add(title);
        Paragraph generated = new Paragraph(
                "Generated "
                        + LocalDate.now().format(FMT),
                BODY_FONT);
        generated.setAlignment(Element.ALIGN_RIGHT);
        generated.setSpacingAfter(8);
        doc.add(generated);
    }

    private static PdfPTable buildTable(
            List<ServiceSchedule> schedules)
            throws Exception {
        PdfPTable table = new PdfPTable(
                new float[]{3, 2, 2, 3, 2, 2});
        table.setWidthPercentage(100);
        addHeaders(table);
        for (ServiceSchedule s : schedules) {
            addRow(table, s);
        }
        return table;
    }

    private static void addHeaders(PdfPTable table) {
        String[] headers = {"Item", "Location",
                "Service Type", "Vendor",
                "Next Due", "Frequency"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(
                    new Phrase(h, HEADER_FONT));
            cell.setBackgroundColor(
                    new java.awt.Color(220, 220, 220));
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private static void addRow(
            PdfPTable table, ServiceSchedule s) {
        addCell(table, s.getItem().getName());
        addCell(table,
                s.getItem().getLocation() != null
                        ? s.getItem().getLocation()
                        : "");
        addCell(table, s.getServiceType());
        addCell(table,
                s.getPreferredVendor() != null
                        ? s.getPreferredVendor().getName()
                        : "");
        addCell(table,
                s.getNextDueDate() != null
                        ? s.getNextDueDate().format(FMT)
                        : "");
        addCell(table,
                "Every " + s.getFrequencyInterval()
                        + " " + s.getFrequencyUnit());
    }

    private static void addCell(
            PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(
                new Phrase(text, BODY_FONT));
        cell.setPadding(4);
        table.addCell(cell);
    }
}

package solutions.mystuff.application.web;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
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

final class ItemHistoryPdf {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final Font TITLE_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 16);
    private static final Font SECTION_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 13);
    private static final Font LABEL_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 10);
    private static final Font BODY_FONT =
            FontFactory.getFont(
                    FontFactory.HELVETICA, 10);
    private static final java.awt.Color HEADER_BG =
            new java.awt.Color(220, 220, 220);

    private ItemHistoryPdf() {
    }

    static void write(
            HttpServletResponse response,
            Item item,
            List<ServiceRecord> records,
            List<ServiceSchedule> schedules,
            String orgName,
            String username) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "inline; filename=item-history.pdf");
        Document doc = new Document(
                PageSize.LETTER.rotate());
        PdfWriter.getInstance(doc,
                response.getOutputStream());
        doc.open();
        addOrgHeader(doc, orgName, username);
        addTitle(doc, orgName, item);
        addItemDetails(doc, item);
        addScheduleSection(doc, schedules);
        addHistorySection(doc, records);
        doc.close();
    }

    private static void addOrgHeader(
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

    private static void addTitle(
            Document doc, String orgName, Item item)
            throws Exception {
        Paragraph title = new Paragraph(
                orgName + " — " + item.getName(),
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

    private static void addItemDetails(
            Document doc, Item item) throws Exception {
        PdfPTable table = new PdfPTable(
                new float[]{1, 3, 1, 3});
        table.setWidthPercentage(100);
        table.setSpacingAfter(12);
        addLabelValue(table, "Location",
                safe(item.getLocation()));
        addLabelValue(table, "Manufacturer",
                safe(item.getManufacturer()));
        addLabelValue(table, "Model",
                safe(item.getModelName()));
        addLabelValue(table, "Serial #",
                safe(item.getSerialNumber()));
        doc.add(table);
    }

    private static void addLabelValue(
            PdfPTable table,
            String label, String value) {
        PdfPCell lbl = new PdfPCell(
                new Phrase(label, LABEL_FONT));
        lbl.setBorder(0);
        lbl.setPadding(3);
        table.addCell(lbl);
        PdfPCell val = new PdfPCell(
                new Phrase(value, BODY_FONT));
        val.setBorder(0);
        val.setPadding(3);
        table.addCell(val);
    }

    private static void addScheduleSection(
            Document doc,
            List<ServiceSchedule> schedules)
            throws Exception {
        Paragraph heading = new Paragraph(
                "Active Schedules", SECTION_FONT);
        heading.setSpacingAfter(6);
        doc.add(heading);
        List<ServiceSchedule> active = schedules
                .stream().filter(ServiceSchedule::isActive)
                .toList();
        if (active.isEmpty()) {
            doc.add(new Paragraph(
                    "No active schedules.", BODY_FONT));
        } else {
            doc.add(buildScheduleTable(active));
        }
    }

    private static PdfPTable buildScheduleTable(
            List<ServiceSchedule> schedules)
            throws Exception {
        PdfPTable table = new PdfPTable(
                new float[]{3, 2, 2, 2, 2});
        table.setWidthPercentage(100);
        table.setSpacingAfter(16);
        addHeaderRow(table, "Service Type", "Vendor",
                "Next Due", "Frequency",
                "Last Completed");
        for (ServiceSchedule s : schedules) {
            addCell(table, s.getServiceType());
            addCell(table,
                    s.getPreferredVendor() != null
                            ? s.getPreferredVendor()
                                    .getName() : "");
            addCell(table,
                    fmtDate(s.getNextDueDate()));
            addCell(table,
                    "Every " + s.getFrequencyInterval()
                            + " " + s.getFrequencyUnit());
            addCell(table,
                    fmtDate(s.getLastCompletedDate()));
        }
        return table;
    }

    private static void addHistorySection(
            Document doc,
            List<ServiceRecord> records)
            throws Exception {
        Paragraph heading = new Paragraph(
                "Service History", SECTION_FONT);
        heading.setSpacingAfter(6);
        doc.add(heading);
        if (records.isEmpty()) {
            doc.add(new Paragraph(
                    "No service records.", BODY_FONT));
        } else {
            doc.add(buildRecordTable(records));
        }
    }

    private static PdfPTable buildRecordTable(
            List<ServiceRecord> records)
            throws Exception {
        PdfPTable table = new PdfPTable(
                new float[]{2, 2, 2, 2, 4});
        table.setWidthPercentage(100);
        addHeaderRow(table, "Date", "Service Type",
                "Vendor", "Tech", "Summary");
        for (ServiceRecord r : records) {
            addCell(table,
                    fmtDate(r.getServiceDate()));
            addCell(table, safe(r.getServiceType()));
            addCell(table,
                    r.getVendor() != null
                            ? r.getVendor().getName()
                            : "");
            addCell(table,
                    extractTech(r.getDescription()));
            addCell(table, safe(r.getSummary()));
        }
        return table;
    }

    private static void addHeaderRow(
            PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(
                    new Phrase(h, LABEL_FONT));
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

    private static String extractTech(String desc) {
        if (desc != null
                && desc.startsWith("Technician: ")) {
            return desc.substring(12);
        }
        return "";
    }
}

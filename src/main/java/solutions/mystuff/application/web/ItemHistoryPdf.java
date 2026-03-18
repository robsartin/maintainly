package solutions.mystuff.application.web;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import solutions.mystuff.domain.model.Item;
import solutions.mystuff.domain.model.ServiceRecord;
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
 * Generates a PDF report of an item's service history
 * and active schedules using OpenPDF.
 *
 * @see ReportController
 * @see PdfHelper
 */
final class ItemHistoryPdf {

    private ItemHistoryPdf() {
    }

    /** Writes the item history PDF to the HTTP response stream. */
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
        PdfHelper.addOrgHeader(doc, orgName, username);
        addTitle(doc, orgName, item);
        addItemDetails(doc, item);
        addScheduleSection(doc, schedules);
        addHistorySection(doc, records);
        doc.close();
    }

    private static void addTitle(
            Document doc, String orgName, Item item)
            throws Exception {
        Paragraph title = new Paragraph(
                orgName + " — " + item.getName(),
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

    private static void addItemDetails(
            Document doc, Item item) throws Exception {
        PdfPTable table = new PdfPTable(
                new float[]{1, 3, 1, 3});
        table.setWidthPercentage(100);
        table.setSpacingAfter(12);
        addLabelValue(table, "Location",
                PdfHelper.safe(item.getLocation()));
        addLabelValue(table, "Manufacturer",
                PdfHelper.safe(item.getManufacturer()));
        addLabelValue(table, "Model",
                PdfHelper.safe(item.getModelName()));
        addLabelValue(table, "Serial #",
                PdfHelper.safe(item.getSerialNumber()));
        doc.add(table);
    }

    private static void addLabelValue(
            PdfPTable table,
            String label, String value) {
        PdfPCell lbl = new PdfPCell(
                new Phrase(label, PdfHelper.LABEL_FONT));
        lbl.setBorder(0);
        lbl.setPadding(3);
        table.addCell(lbl);
        PdfPCell val = new PdfPCell(
                new Phrase(value, PdfHelper.BODY_FONT));
        val.setBorder(0);
        val.setPadding(3);
        table.addCell(val);
    }

    private static void addScheduleSection(
            Document doc,
            List<ServiceSchedule> schedules)
            throws Exception {
        Paragraph heading = new Paragraph(
                "Active Schedules",
                PdfHelper.SECTION_FONT);
        heading.setSpacingAfter(6);
        doc.add(heading);
        List<ServiceSchedule> active = schedules
                .stream().filter(ServiceSchedule::isActive)
                .toList();
        if (active.isEmpty()) {
            doc.add(new Paragraph(
                    "No active schedules.",
                    PdfHelper.BODY_FONT));
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
        PdfHelper.addHeaderRow(table, "Service Type",
                "Vendor", "Next Due", "Frequency",
                "Last Completed");
        for (ServiceSchedule s : schedules) {
            PdfHelper.addCell(table, s.getServiceType());
            PdfHelper.addCell(table,
                    s.getPreferredVendor() != null
                            ? s.getPreferredVendor()
                                    .getName() : "");
            PdfHelper.addCell(table,
                    PdfHelper.fmtDate(
                            s.getNextDueDate()));
            PdfHelper.addCell(table,
                    "Every " + s.getFrequencyInterval()
                            + " " + s.getFrequencyUnit());
            PdfHelper.addCell(table,
                    PdfHelper.fmtDate(
                            s.getLastCompletedDate()));
        }
        return table;
    }

    private static void addHistorySection(
            Document doc,
            List<ServiceRecord> records)
            throws Exception {
        Paragraph heading = new Paragraph(
                "Service History",
                PdfHelper.SECTION_FONT);
        heading.setSpacingAfter(6);
        doc.add(heading);
        if (records.isEmpty()) {
            doc.add(new Paragraph(
                    "No service records.",
                    PdfHelper.BODY_FONT));
        } else {
            doc.add(buildRecordTable(records));
        }
    }

    private static PdfPTable buildRecordTable(
            List<ServiceRecord> records)
            throws Exception {
        PdfPTable table = new PdfPTable(
                new float[]{2, 2, 2, 2, 1, 3});
        table.setWidthPercentage(100);
        PdfHelper.addHeaderRow(table, "Date",
                "Service Type", "Vendor", "Tech",
                "Cost", "Summary");
        for (ServiceRecord r : records) {
            addRecordRow(table, r);
        }
        return table;
    }

    private static void addRecordRow(
            PdfPTable table, ServiceRecord r) {
        PdfHelper.addCell(table,
                PdfHelper.fmtDate(r.getServiceDate()));
        PdfHelper.addCell(table,
                PdfHelper.safe(r.getServiceType()));
        PdfHelper.addCell(table,
                r.getVendor() != null
                        ? r.getVendor().getName()
                        : "");
        PdfHelper.addCell(table,
                PdfHelper.safe(
                        r.getTechnicianName()));
        PdfHelper.addCell(table, formatCost(r.getCost()));
        PdfHelper.addCell(table,
                PdfHelper.safe(r.getSummary()));
    }

    private static String formatCost(BigDecimal cost) {
        if (cost == null
                || cost.compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }
        return "$" + cost.setScale(2,
                RoundingMode.HALF_UP)
                .toPlainString();
    }
}

package com.miniproject.qpgenerator.pdf.service;


import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    public byte[] generateQuestionPaper(Map<String, Object> data) throws Exception {

        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font bold = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 10);

        // ================= FETCH DATA =================
        String examType = get(data, "examType");
        String subjectCode = get(data, "subjectCode");
        String subjectTitle = get(data, "subjectTitle");
        String department = get(data, "department");
        String yearSem = get(data, "yearSem");
        String date = get(data, "date");

        int marks = examType.equalsIgnoreCase("MODEL") ? 100 : 50;
        String time = examType.equalsIgnoreCase("MODEL") ? "3 Hours" : "1.30 Hours";

        // ================= HEADER =================
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{20, 80});

        PdfPCell logoCell = noBorderCell();
        try {
            URL logoUrl = getClass().getClassLoader().getResource("logo.jpeg");
            if (logoUrl != null) {
                Image logo = Image.getInstance(logoUrl);
                logo.scaleToFit(70, 70);
                logoCell.addElement(logo);
            }
        } catch (Exception ignored) {}

        header.addCell(logoCell);

        PdfPCell collegeCell = noBorderCell();
        collegeCell.addElement(new Paragraph("Velammal Engineering College", bold));
        collegeCell.addElement(new Paragraph("(An Autonomous Institution, Affiliated to Anna University – Chennai)", normal));
        collegeCell.addElement(new Paragraph("Velammal Newgen Park Ambattur – RedHills Road , Chennai-600 066", normal));
        header.addCell(collegeCell);

        document.add(header);
        document.add(space());

        Paragraph examPara = new Paragraph(examType + " EXAMINATION", bold);
        examPara.setAlignment(Element.ALIGN_CENTER);
        document.add(examPara);
        document.add(space());

        // ================= SUBJECT DETAILS TABLE (1,5,2,2) =================
        PdfPTable details = new PdfPTable(4);
        details.setWidthPercentage(100);
        details.setSpacingBefore(5f);
        details.setSpacingAfter(10f);
        details.setWidths(new float[]{6, 16, 3, 6});

        addRow(details, "Subject Code", subjectCode, "Marks", String.valueOf(marks));
        addRow(details, "Subject Title", subjectTitle, "Date", date);
        addRow(details, "Department", department, "Time", time);
        addRow(details, "Year / Sem", yearSem, "", "");

        document.add(details);
        document.add(space());

        Map<String, Object> firstSet =
                ((List<Map<String, Object>>) data.get("sets")).get(0);

        List<Map<String, Object>> partA =
                (List<Map<String, Object>>) firstSet.get("partA");

        List<Map<String, Object>> partB =
                (List<Map<String, Object>>) firstSet.get("partB");

        // ================= PART A (2, 24, 3, 2) =================
        document.add(new Paragraph("PART A", bold));
        document.add(space());

        PdfPTable partATable = new PdfPTable(4);
        partATable.setWidthPercentage(100);
        partATable.setSpacingBefore(5f);
        partATable.setSpacingAfter(10f);
        partATable.setWidths(new float[]{2, 24, 3, 2});

        int sNo = 1;
        for (Map<String, Object> q : partA) {
            partATable.addCell(tableCell(String.valueOf(sNo)));
            partATable.addCell(tableCell(get(q, "questionText")));
            partATable.addCell(tableCell("CO" + get(q, "unit")));
            partATable.addCell(tableCell("C" + get(q, "bloomLevel")));
            sNo++;
        }

        document.add(partATable);
        document.add(space());

        // ================= PART B (2, 2, 2, 18, 2, 3, 2) =================
        document.add(new Paragraph("PART B", bold));
        document.add(space());

        PdfPTable partBTable = new PdfPTable(7);
        partBTable.setWidthPercentage(100);
        partBTable.setSpacingBefore(5f);
        partBTable.setSpacingAfter(10f);
        partBTable.setWidths(new float[]{2, 2, 2, 18, 2, 3, 2});

        Map<Integer, List<Map<String, Object>>> grouped =
                partB.stream().collect(Collectors.groupingBy(
                        q -> (Integer) q.get("orderIndex"),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        for (Map.Entry<Integer, List<Map<String, Object>>> entry : grouped.entrySet()) {

            Integer mainNo = entry.getKey();
            List<Map<String, Object>> questions = entry.getValue();

            List<Map<String, Object>> aParts = questions.stream()
                    .filter(q -> get(q, "subPart").startsWith("a"))
                    .collect(Collectors.toList());

            List<Map<String, Object>> bParts = questions.stream()
                    .filter(q -> get(q, "subPart").startsWith("b"))
                    .collect(Collectors.toList());

            for (Map<String, Object> q : aParts) {
                addPartBRow(partBTable, mainNo, "a", q);
            }

            if (!aParts.isEmpty() && !bParts.isEmpty()) {
                PdfPCell orCell = tableCell("OR");
                orCell.setColspan(7);
                orCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                partBTable.addCell(orCell);
            }

            for (Map<String, Object> q : bParts) {
                addPartBRow(partBTable, mainNo, "b", q);
            }
        }

        document.add(partBTable);

        document.close();
        return out.toByteArray();
    }

    // ================= HELPER METHODS =================

    private void addPartBRow(PdfPTable table, Integer mainNo, String option, Map<String, Object> q) {

        String sub = get(q, "subPart");
        String subDisplay = "";

        if (sub.contains(".")) {
            subDisplay = sub.substring(sub.indexOf('.') + 1);
        }

        table.addCell(tableCell(String.valueOf(mainNo)));
        table.addCell(tableCell(option));
        table.addCell(tableCell(subDisplay));
        table.addCell(tableCell(get(q, "questionText")));
        table.addCell(tableCell(get(q, "marks")));
        table.addCell(tableCell("CO" + get(q, "unit")));
        table.addCell(tableCell("C" + get(q, "bloomLevel")));
    }

    private void addRow(PdfPTable table, String l1, String v1, String l2, String v2) {
        table.addCell(tableCell(l1));
        table.addCell(tableCell(v1));
        table.addCell(tableCell(l2));
        table.addCell(tableCell(v2));
    }

    private PdfPCell tableCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", new Font(Font.HELVETICA, 10)));
        cell.setPadding(6f);  // 👈 Space inside table cells
        return cell;
    }

    private PdfPCell noBorderCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private Paragraph space() {
        return new Paragraph(" ");
    }

    private String get(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
}
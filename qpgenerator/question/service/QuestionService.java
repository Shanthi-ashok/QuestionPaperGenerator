package com.miniproject.qpgenerator.question.service;

import com.miniproject.qpgenerator.model.Question;
import com.miniproject.qpgenerator.question.dto.ExcelUploadRequest;
import com.miniproject.qpgenerator.question.dto.ExcelUploadResponse;
import com.miniproject.qpgenerator.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;

    public ExcelUploadResponse uploadQuestions(ExcelUploadRequest request) throws IOException {
        MultipartFile file = request.getFile();

        // ✅ Reads ALL 5 columns FROM EXCEL: question_text,subject_code,unit,marks,bloom_level
        List<Question> validQuestions = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int totalRows = 0;
        int validRows = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                totalRows++;

                if (row.getRowNum() == 0) continue; // Skip header row

                try {
                    Question question = parseRow(row); // ✅ Parses 5 columns from Excel
                    if (question != null) {
                        validQuestions.add(question);
                        validRows++;
                    }
                } catch (Exception e) {
                    errors.add("Row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                }
            }
        }

        // Batch save
        if (!validQuestions.isEmpty()) {
            questionRepository.saveAll(validQuestions);
        }

        return ExcelUploadResponse.builder()
                .totalRows(totalRows - 1)
                .validRows(validRows)
                .skippedRows(totalRows - 1 - validRows)
                .errors(errors.size())
                .message(validRows + " questions uploaded successfully")
                .build();
    }

    private Question parseRow(Row row) {
        try {
            // ✅ EXACT 5-COLUMN MAPPING (0-indexed):
            // Col 0: question_text | Col 1: subject_code | Col 2: unit | Col 3: marks | Col 4: bloom_level
            String questionText = getCellValue(row.getCell(0)).trim();
            String subjectCode = getCellValue(row.getCell(1)).trim().toUpperCase();
            Integer unit = Integer.valueOf(getCellValue(row.getCell(2)).trim());
            Integer marks = Integer.valueOf(getCellValue(row.getCell(3)).trim());
            Integer bloomLevel = Integer.valueOf(getCellValue(row.getCell(4)).trim());

            // ✅ VALIDATION
            validateQuestionData(questionText, subjectCode, unit, marks, bloomLevel);

            // ✅ usageCount = 0 AUTOMATICALLY (via @Builder.Default)
            return Question.builder()
                    .questionText(questionText)
                    .subjectCode(subjectCode)
                    .unit(unit)
                    .marks(marks)
                    .bloomLevel(bloomLevel)
                    // usageCount=0 and status="ACTIVE" set automatically by @Builder.Default
                    .facultyId(1L) // Default faculty
                    .build();

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in columns 3,4, or 5");
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void validateQuestionData(String questionText, String subjectCode, Integer unit, Integer marks, Integer bloomLevel) {
        if (questionText.length() < 10) {
            throw new IllegalArgumentException("Question text too short (min 10 chars)");
        }
        if (subjectCode.length() < 2 || subjectCode.length() > 20) {
            throw new IllegalArgumentException("Subject code must be 2-20 chars");
        }
        if (unit < 1 || unit > 5) {
            throw new IllegalArgumentException("Unit must be 1-5, got: " + unit);
        }
        if (!Arrays.asList(2, 5, 7, 8, 10, 15, 16).contains(marks)) {
            throw new IllegalArgumentException("Marks must be 2,5,7,8,10,15,16, got: " + marks);
        }
        if (bloomLevel < 1 || bloomLevel > 4) {
            throw new IllegalArgumentException("Bloom level must be 1-4, got: " + bloomLevel);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}

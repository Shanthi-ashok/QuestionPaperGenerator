package com.miniproject.qpgenerator.question.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExcelUploadResponse {
    private String subjectCode;
    private int totalRows;
    private int validRows;
    private int skippedRows;
    private int errors;
    private String message;
}

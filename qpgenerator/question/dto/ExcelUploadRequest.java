package com.miniproject.qpgenerator.question.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ExcelUploadRequest {
    private MultipartFile file;
    // ✅ subjectCode reads from Excel column 1 - field is optional
}

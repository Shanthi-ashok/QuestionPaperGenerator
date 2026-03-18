package com.miniproject.qpgenerator.question.controller;

import com.miniproject.qpgenerator.question.dto.ExcelUploadRequest;
import com.miniproject.qpgenerator.question.dto.ExcelUploadResponse;
import com.miniproject.qpgenerator.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/upload")
    public ResponseEntity<ExcelUploadResponse> uploadQuestions(
            @ModelAttribute ExcelUploadRequest request) {
        try {
            ExcelUploadResponse response = questionService.uploadQuestions(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ExcelUploadResponse errorResponse = ExcelUploadResponse.builder()
                    .totalRows(0)
                    .validRows(0)
                    .skippedRows(0)
                    .errors(1)
                    .message("Upload failed: " + e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}

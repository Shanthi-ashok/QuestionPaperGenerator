package com.miniproject.qpgenerator.pdf.controller;

import com.miniproject.qpgenerator.pdf.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf(
            @RequestBody Map<String, Object> data) throws Exception {

        byte[] pdf = pdfService.generateQuestionPaper(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=QuestionPaper.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
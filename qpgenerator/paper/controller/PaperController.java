package com.miniproject.qpgenerator.paper.controller;

import com.miniproject.qpgenerator.paper.dto.GeneratePaperRequest;
import com.miniproject.qpgenerator.paper.dto.PaperResponse;
import com.miniproject.qpgenerator.paper.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
public class PaperController {

    private final PaperService paperService;

    @PostMapping("/generate")
    public ResponseEntity<PaperResponse> generatePaper(@RequestBody GeneratePaperRequest request) {
        PaperResponse response = paperService.generatePaper(request);
        return ResponseEntity.ok(response);
    }
}

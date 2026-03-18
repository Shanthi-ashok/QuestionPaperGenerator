package com.miniproject.qpgenerator.paper.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaperResponse {
    private Long paperId;
    private String subjectCode;
    private String examType;
    private List<SetResponse> sets;
}

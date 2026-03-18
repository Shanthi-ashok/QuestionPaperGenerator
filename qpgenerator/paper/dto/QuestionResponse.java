package com.miniproject.qpgenerator.paper.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResponse {
    private Long questionId;
    private String questionText;
    private Integer unit;
    private Integer marks;
    private Integer bloomLevel;
    private String subPart; // a, b, null
    private Integer orderIndex;
}

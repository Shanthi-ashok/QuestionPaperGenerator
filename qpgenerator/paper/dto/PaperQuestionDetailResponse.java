package com.miniproject.qpgenerator.paper.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaperQuestionDetailResponse {
    private Long questionId;
    private String questionText;
    private String part; // A or B
    private String subPart; // a, b, b.i, b.ii, etc.
    private Integer orderIndex;
    private Integer unit;
    private Integer marks;
    private Integer bloomLevel;
}

package com.miniproject.qpgenerator.paper.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SetResponse {
    private Integer setNumber;
    private List<QuestionResponse> partA; // 10 x 2-mark questions
    private List<QuestionResponse> partB; // 15/16 mark questions per unit
}

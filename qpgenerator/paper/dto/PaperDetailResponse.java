package com.miniproject.qpgenerator.paper.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaperDetailResponse {
    private Long paperId;
    private String subjectCode;
    private String examType;
    private String date;
    private String time;
    private String yearSem;
    private Integer setsCount;
    private Long facultyId;
    private LocalDateTime createdAt;
}

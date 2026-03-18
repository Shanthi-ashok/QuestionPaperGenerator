package com.miniproject.qpgenerator.paper.dto;

import lombok.Data;

import java.util.Map;

@Data
public class GeneratePaperRequest {
    private String subjectCode;
    private String examType; // INTERNAL1, INTERNAL2, MODEL
    private String date;
    private String time;
    private String yearSem;
    private Integer numberOfSets; // 1-10
    private Map<Integer, Integer> unitBloomPreferences; // unit -> bloomLevel
}

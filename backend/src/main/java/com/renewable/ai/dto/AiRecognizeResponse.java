package com.renewable.ai.dto;
import lombok.Data;

@Data
public class AiRecognizeResponse {
    private String itemName;
    private String category;
    private Integer confidence;
    private String advice;
    private String environmentalTips;
    private String abTestGroup;
}

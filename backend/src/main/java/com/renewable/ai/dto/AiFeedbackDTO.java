package com.renewable.ai.dto;
import lombok.Data;

@Data
public class AiFeedbackDTO {
    private String originalImageUrl;
    private String predictedType;
    private String userCorrectedType;
    private Integer confidence;
}

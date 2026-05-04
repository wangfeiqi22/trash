package com.renewable.ai.controller;

import com.renewable.ai.config.AiProperties;
import com.renewable.ai.dto.AiFeedbackDTO;
import com.renewable.ai.dto.AiRecognizeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    @Autowired
    private AiProperties aiProperties;

    private final Random random = new Random();
    private final String[] categories = {"recyclable", "hazardous", "wet", "dry"};
    private final String[] itemNames = {"塑料瓶", "废电池", "剩饭剩菜", "脏纸巾"};

    @PostMapping("/recognize-waste")
    public ResponseEntity<?> recognizeWaste(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "userId", required = false) Long userId) {
        if (!aiProperties.isEnabled()) {
            return ResponseEntity.badRequest().body("AI识别服务已禁用，请联系管理员启用");
        }
        if ("mock".equals(aiProperties.getProvider())) {
            return handleMockRecognition(userId);
        }
        return handleRealRecognition(file, userId);
    }

    private ResponseEntity<AiRecognizeResponse> handleMockRecognition(Long userId) {
        try { Thread.sleep(1500); } catch (InterruptedException e) {}
        AiRecognizeResponse response = new AiRecognizeResponse();
        int idx = random.nextInt(categories.length);
        response.setItemName(itemNames[idx]);
        response.setCategory(categories[idx]);
        response.setConfidence(50 + random.nextInt(50));
        fillAdvice(response);
        response.setAbTestGroup((userId != null && userId % 2 != 0) ? "A_Standard" : "B_Enhanced_V2");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> handleRealRecognition(MultipartFile file, Long userId) {
        log.info("Real AI recognition requested: provider={}", aiProperties.getProvider());
        return ResponseEntity.ok().body("AI recognition with provider '" + aiProperties.getProvider() + "' not yet implemented");
    }

    private void fillAdvice(AiRecognizeResponse response) {
        String category = response.getCategory();
        if ("recyclable".equals(category)) {
            response.setAdvice("请倒空液体后压扁投入可回收物收集容器。");
            response.setEnvironmentalTips("回收1吨塑料可节约石油6吨，减少二氧化碳排放3吨！");
        } else if ("hazardous".equals(category)) {
            response.setAdvice("请轻放，防止破损导致有害物质泄漏。");
            response.setEnvironmentalTips("有害垃圾包含重金属，随意丢弃会严重污染土壤和地下水！");
        } else if ("wet".equals(category)) {
            response.setAdvice("请沥干水分后投放。");
            response.setEnvironmentalTips("湿垃圾可发酵成有机肥料，变废为宝！");
        } else {
            response.setAdvice("请直接投入干垃圾桶。");
            response.setEnvironmentalTips("干垃圾将进行无害化焚烧发电！");
        }
    }

    @PostMapping("/feedback")
    public ResponseEntity<String> submitFeedback(@RequestBody AiFeedbackDTO feedback) {
        log.info("AI feedback received: predictedType={}, userCorrectedType={}",
            feedback.getPredictedType(), feedback.getUserCorrectedType());
        return ResponseEntity.ok("Feedback received and stored for model optimization.");
    }
}

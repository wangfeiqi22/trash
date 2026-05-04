package com.renewable.ai.controller;

import com.renewable.ai.dto.ImageSignRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/image")
@CrossOrigin(origins = "*")
public class ImageSignController {

    private static final String SECRET_KEY = "renewable-java-secret-key-for-image-signing";

    @PostMapping("/sign")
    public ResponseEntity<Map<String, String>> signImage(@RequestBody ImageSignRequest request) {
        try {
            // Construct payload: hash|userId|orderNo|timestamp
            // GPS fields (lat/lon) removed as per requirement
            String payload = String.format("%s|%s|%s|%s",
                    request.getHash(),
                    request.getUserId(),
                    request.getOrderNo(),
                    request.getTimestamp());

            // HMAC-SHA256
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8)));

            Map<String, String> response = new HashMap<>();
            response.put("signature", signature);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    public static boolean verifySignature(String payload, String signature) {
         try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            
            String expectedSignature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
            return expectedSignature.equals(signature);
         } catch (Exception e) {
             return false;
         }
    }
}
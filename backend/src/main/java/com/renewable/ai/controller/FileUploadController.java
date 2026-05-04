package com.renewable.ai.controller;

import com.renewable.ai.common.ApiResponse;
import com.renewable.ai.entity.OrderPhoto;
import com.renewable.ai.service.FileStorageService;
import com.renewable.ai.service.FileValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileValidationService fileValidationService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<OrderPhoto>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("orderId") Long orderId,
            @RequestParam("nodeType") String nodeType) {
        fileValidationService.validate(file);
        OrderPhoto photo = fileStorageService.storeFile(file, orderId, nodeType);
        return ResponseEntity.ok(ApiResponse.success(photo));
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        String cleanName = Paths.get(filename).getFileName().toString();
        File file = Paths.get("uploads", cleanName).toFile();
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = "application/octet-stream";
        if (cleanName.endsWith(".jpg") || cleanName.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (cleanName.endsWith(".png")) {
            contentType = "image/png";
        } else if (cleanName.endsWith(".pdf")) {
            contentType = "application/pdf";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + cleanName + "\"")
                .body(new FileSystemResource(file));
    }
}

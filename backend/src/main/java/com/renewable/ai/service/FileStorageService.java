package com.renewable.ai.service;

import com.renewable.ai.entity.OrderPhoto;
import com.renewable.ai.repository.OrderPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads");

    @Autowired
    private OrderPhotoRepository orderPhotoRepository;

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public String storeUserDocument(MultipartFile file, String docType) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store document", e);
        }
    }

    public OrderPhoto storeFile(MultipartFile file, Long orderId, String nodeType) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));

            OrderPhoto photo = new OrderPhoto();
            photo.setOrderId(orderId);
            photo.setNodeType(nodeType);
            photo.setFileUrl("/uploads/" + filename);
            return orderPhotoRepository.save(photo);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}

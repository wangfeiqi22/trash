package com.renewable.ai.controller;

import com.renewable.ai.common.ApiResponse;
import com.renewable.ai.entity.User;
import com.renewable.ai.entity.Fleet;
import com.renewable.ai.service.TokenService;
import com.renewable.ai.service.UserService;
import com.renewable.ai.service.FileStorageService;
import com.renewable.ai.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FleetService fleetService;

    @GetMapping("/fleets-options")
    public ResponseEntity<List<Fleet>> fleetsOptions() {
        return ResponseEntity.ok(fleetService.getAllFleets());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        return ResponseEntity.ok(ApiResponse.success(userService.register(user)));
    }

    @PostMapping(value = "/register-with-docs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<User>> registerWithDocs(
            @RequestPart("user") User user,
            @RequestPart(name = "businessLicense", required = false) MultipartFile businessLicense,
            @RequestPart(name = "companyIdCard", required = false) MultipartFile companyIdCard,
            @RequestPart(name = "driverIdCard", required = false) MultipartFile driverIdCard,
            @RequestPart(name = "driverLicense", required = false) MultipartFile driverLicense
    ) {
        if (businessLicense != null && !businessLicense.isEmpty()) {
            String url = fileStorageService.storeUserDocument(businessLicense, "company-license");
            user.setCompanyLicenseUrl(url);
        }
        if (companyIdCard != null && !companyIdCard.isEmpty()) {
            String url = fileStorageService.storeUserDocument(companyIdCard, "company-idcard");
            user.setCompanyLegalIdCardUrl(url);
        }
        if (driverIdCard != null && !driverIdCard.isEmpty()) {
            String url = fileStorageService.storeUserDocument(driverIdCard, "driver-idcard");
            user.setDriverIdCardUrl(url);
        }
        if (driverLicense != null && !driverLicense.isEmpty()) {
            String url = fileStorageService.storeUserDocument(driverLicense, "driver-license");
            user.setDriverLicenseUrl(url);
        }
        return ResponseEntity.ok(ApiResponse.success(userService.register(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> payload) {
        User user = userService.login(payload.get("username"), payload.get("password"));
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("用户名或密码错误"));
        }
        String accessToken = tokenService.generateAccessToken(user.getId(), user.getRole(), user.getUsername());
        String refreshToken = tokenService.generateRefreshToken();
        tokenService.storeRefreshToken(refreshToken, user.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refresh(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("刷新令牌不能为空"));
        }
        Long userId = tokenService.validateRefreshToken(refreshToken);
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("刷新令牌无效或已过期"));
        }
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("用户不存在"));
        }
        String newAccessToken = tokenService.generateAccessToken(user.getId(), user.getRole(), user.getUsername());
        String newRefreshToken = tokenService.generateRefreshToken();
        tokenService.revokeRefreshToken(refreshToken);
        tokenService.storeRefreshToken(newRefreshToken, user.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("accessToken", newAccessToken);
        data.put("refreshToken", newRefreshToken);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");
        if (refreshToken != null && !refreshToken.isBlank()) {
            tokenService.revokeRefreshToken(refreshToken);
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

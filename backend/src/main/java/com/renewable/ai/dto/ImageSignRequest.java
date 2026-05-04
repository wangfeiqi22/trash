package com.renewable.ai.dto;

public class ImageSignRequest {
    private String hash;
    private Long userId;
    private String orderNo;
    private String timestamp;
    private String lat;
    private String lon;

    // Getters and Setters
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }
    
    public String getLon() { return lon; }
    public void setLon(String lon) { this.lon = lon; }
}
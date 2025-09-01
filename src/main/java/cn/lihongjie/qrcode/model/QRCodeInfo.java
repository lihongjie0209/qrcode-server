package cn.lihongjie.qrcode.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * QR码信息，包含解码内容和位置信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QRCodeInfo {
    
    private String content;
    private BoundingBox boundingBox;
    private double confidence;
    
    public QRCodeInfo() {}
    
    public QRCodeInfo(String content, BoundingBox boundingBox) {
        this.content = content;
        this.boundingBox = boundingBox;
    }
    
    public QRCodeInfo(String content, BoundingBox boundingBox, double confidence) {
        this.content = content;
        this.boundingBox = boundingBox;
        this.confidence = confidence;
    }
    
    // Getters and setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
    
    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}

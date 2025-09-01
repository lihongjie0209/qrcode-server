package cn.lihongjie.qrcode.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 处理时间信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessingTime {
    
    private long totalTimeMs;
    private long imageLoadTimeMs;
    private long detectionTimeMs;
    private long fileSizeBytes;
    private String imageFormat;
    private int imageWidth;
    private int imageHeight;
    
    public ProcessingTime() {}
    
    public ProcessingTime(long totalTimeMs, long imageLoadTimeMs, long detectionTimeMs) {
        this.totalTimeMs = totalTimeMs;
        this.imageLoadTimeMs = imageLoadTimeMs;
        this.detectionTimeMs = detectionTimeMs;
    }
    
    // Getters and setters
    public long getTotalTimeMs() {
        return totalTimeMs;
    }
    
    public void setTotalTimeMs(long totalTimeMs) {
        this.totalTimeMs = totalTimeMs;
    }
    
    public long getImageLoadTimeMs() {
        return imageLoadTimeMs;
    }
    
    public void setImageLoadTimeMs(long imageLoadTimeMs) {
        this.imageLoadTimeMs = imageLoadTimeMs;
    }
    
    public long getDetectionTimeMs() {
        return detectionTimeMs;
    }
    
    public void setDetectionTimeMs(long detectionTimeMs) {
        this.detectionTimeMs = detectionTimeMs;
    }
    
    public long getFileSizeBytes() {
        return fileSizeBytes;
    }
    
    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }
    
    public String getImageFormat() {
        return imageFormat;
    }
    
    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }
    
    public int getImageWidth() {
        return imageWidth;
    }
    
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }
    
    public int getImageHeight() {
        return imageHeight;
    }
    
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
    
    /**
     * 格式化文件大小为可读格式
     */
    public String getFormattedFileSize() {
        if (fileSizeBytes < 1024) {
            return fileSizeBytes + " B";
        } else if (fileSizeBytes < 1024 * 1024) {
            return String.format("%.1f KB", fileSizeBytes / 1024.0);
        } else {
            return String.format("%.1f MB", fileSizeBytes / (1024.0 * 1024.0));
        }
    }
    
    /**
     * 获取总像素数
     */
    public long getTotalPixels() {
        return (long) imageWidth * imageHeight;
    }
}

package cn.lihongjie.qrcode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * QR码检测结果
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QRCodeResult {
    
    private boolean success;
    private String message;
    private List<QRCodeInfo> qrCodes;
    private int count;
    private ProcessingTime processingTime;
    
    public QRCodeResult() {}
    
    public QRCodeResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public QRCodeResult(boolean success, List<QRCodeInfo> qrCodes) {
        this.success = success;
        this.qrCodes = qrCodes;
        this.count = qrCodes != null ? qrCodes.size() : 0;
    }
    
    public QRCodeResult(boolean success, List<QRCodeInfo> qrCodes, ProcessingTime processingTime) {
        this.success = success;
        this.qrCodes = qrCodes;
        this.count = qrCodes != null ? qrCodes.size() : 0;
        this.processingTime = processingTime;
    }
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<QRCodeInfo> getQrCodes() {
        return qrCodes;
    }
    
    public void setQrCodes(List<QRCodeInfo> qrCodes) {
        this.qrCodes = qrCodes;
        this.count = qrCodes != null ? qrCodes.size() : 0;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public ProcessingTime getProcessingTime() {
        return processingTime;
    }
    
    public void setProcessingTime(ProcessingTime processingTime) {
        this.processingTime = processingTime;
    }
}

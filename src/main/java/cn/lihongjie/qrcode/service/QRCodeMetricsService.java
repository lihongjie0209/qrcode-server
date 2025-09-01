package cn.lihongjie.qrcode.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * QR码检测指标服务
 */
@ApplicationScoped
public class QRCodeMetricsService {
    
    @Inject
    MeterRegistry meterRegistry;
    
    // 计数器
    private Counter detectionRequestCounter;
    private Counter successfulDetectionCounter;
    private Counter failedDetectionCounter;
    private Counter totalQrCodesDetectedCounter;
    
    // 计时器
    private Timer imageLoadTimer;
    private Timer qrDetectionTimer;
    private Timer totalProcessingTimer;
    
    // 初始化方法
    public void init() {
        // 初始化计数器
        detectionRequestCounter = Counter.builder("qrcode.detection.requests.total")
                .description("Total number of QR code detection requests")
                .register(meterRegistry);
                
        successfulDetectionCounter = Counter.builder("qrcode.detection.success.total")
                .description("Total number of successful QR code detections")
                .register(meterRegistry);
                
        failedDetectionCounter = Counter.builder("qrcode.detection.failed.total")
                .description("Total number of failed QR code detections")
                .register(meterRegistry);
                
        totalQrCodesDetectedCounter = Counter.builder("qrcode.detected.total")
                .description("Total number of QR codes detected")
                .register(meterRegistry);
        
        // 初始化计时器
        imageLoadTimer = Timer.builder("qrcode.image.load.duration")
                .description("Time taken to load and parse image")
                .register(meterRegistry);
                
        qrDetectionTimer = Timer.builder("qrcode.detection.duration")
                .description("Time taken to detect QR codes")
                .register(meterRegistry);
                
        totalProcessingTimer = Timer.builder("qrcode.processing.total.duration")
                .description("Total time taken to process QR code detection request")
                .register(meterRegistry);
    }
    
    // 计数器方法
    public void incrementDetectionRequest() {
        if (detectionRequestCounter == null) init();
        detectionRequestCounter.increment();
    }
    
    public void incrementSuccessfulDetection() {
        if (successfulDetectionCounter == null) init();
        successfulDetectionCounter.increment();
    }
    
    public void incrementFailedDetection() {
        if (failedDetectionCounter == null) init();
        failedDetectionCounter.increment();
    }
    
    public void incrementQrCodesDetected(int count) {
        if (totalQrCodesDetectedCounter == null) init();
        totalQrCodesDetectedCounter.increment(count);
    }
    
    // 计时器方法
    public Timer.Sample startImageLoadTimer() {
        if (imageLoadTimer == null) init();
        return Timer.start(meterRegistry);
    }
    
    public void stopImageLoadTimer(Timer.Sample sample) {
        if (imageLoadTimer == null) init();
        sample.stop(imageLoadTimer);
    }
    
    public Timer.Sample startQrDetectionTimer() {
        if (qrDetectionTimer == null) init();
        return Timer.start(meterRegistry);
    }
    
    public void stopQrDetectionTimer(Timer.Sample sample) {
        if (qrDetectionTimer == null) init();
        sample.stop(qrDetectionTimer);
    }
    
    public Timer.Sample startTotalProcessingTimer() {
        if (totalProcessingTimer == null) init();
        return Timer.start(meterRegistry);
    }
    
    public void stopTotalProcessingTimer(Timer.Sample sample) {
        if (totalProcessingTimer == null) init();
        sample.stop(totalProcessingTimer);
    }
    
    // 记录文件大小分布
    public void recordFileSize(long fileSizeBytes) {
        if (meterRegistry != null) {
            meterRegistry.gauge("qrcode.image.size.bytes", fileSizeBytes);
        }
    }
    
    // 记录图片尺寸
    public void recordImageDimensions(int width, int height) {
        if (meterRegistry != null) {
            meterRegistry.gauge("qrcode.image.width.pixels", width);
            meterRegistry.gauge("qrcode.image.height.pixels", height);
        }
    }
}

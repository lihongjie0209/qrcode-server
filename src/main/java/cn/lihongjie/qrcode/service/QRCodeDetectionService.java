package cn.lihongjie.qrcode.service;

import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import cn.lihongjie.qrcode.model.BoundingBox;
import cn.lihongjie.qrcode.model.Point;
import cn.lihongjie.qrcode.model.ProcessingTime;
import cn.lihongjie.qrcode.model.QRCodeInfo;
import cn.lihongjie.qrcode.model.QRCodeResult;
import georegression.struct.point.Point2D_F64;
import georegression.struct.shapes.Polygon2D_F64;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * QR码检测服务
 */
@ApplicationScoped
public class QRCodeDetectionService {
    
    private static final Logger LOG = Logger.getLogger(QRCodeDetectionService.class);
    
    private final QrCodeDetector<GrayU8> detector;
    
    @Inject
    QRCodeMetricsService metricsService;
    
    public QRCodeDetectionService() {
        // 创建QR码检测器
        this.detector = FactoryFiducial.qrcode(null, GrayU8.class);
    }
    
    /**
     * 从图片字节数组中检测QR码 - 优化版本，直接从字节数组加载
     * 
     * @param imageBytes 图片字节数组
     * @return 检测结果
     */
    public QRCodeResult detectQRCodes(byte[] imageBytes) {
        long startTime = System.currentTimeMillis();
        Timer.Sample totalProcessingSample = metricsService.startTotalProcessingTimer();
        metricsService.incrementDetectionRequest();
        
        ProcessingTime processingTime = new ProcessingTime();
        processingTime.setFileSizeBytes(imageBytes.length);
        
        try {
            // 记录文件大小
            metricsService.recordFileSize(imageBytes.length);
            
            // 直接从字节数组加载为BoofCV图像 - 优化的加载方式
            long imageLoadStart = System.currentTimeMillis();
            Timer.Sample imageLoadSample = metricsService.startImageLoadTimer();
            
            // 直接加载图片为GrayU8，不进行预处理
            GrayU8 grayImage = loadImageSimpleDirect(imageBytes, processingTime);
            
            long imageLoadEnd = System.currentTimeMillis();
            metricsService.stopImageLoadTimer(imageLoadSample);
            
            long imageLoadTime = imageLoadEnd - imageLoadStart;
            processingTime.setImageLoadTimeMs(imageLoadTime);
            
            if (grayImage == null) {
                metricsService.incrementFailedDetection();
                return new QRCodeResult(false, "无法读取图片文件");
            }
            
            // 记录图片尺寸
            metricsService.recordImageDimensions(grayImage.getWidth(), grayImage.getHeight());
            
            QRCodeResult result = detectQRCodesFromGrayImage(grayImage, processingTime);
            
            // 计算总时间
            long totalTime = System.currentTimeMillis() - startTime;
            processingTime.setTotalTimeMs(totalTime);
            result.setProcessingTime(processingTime);
            
            if (result.isSuccess()) {
                metricsService.incrementSuccessfulDetection();
                metricsService.incrementQrCodesDetected(result.getCount());
            } else {
                metricsService.incrementFailedDetection();
            }
            
            return result;
            
        } catch (Exception e) {
            LOG.error("处理图片时出错", e);
            metricsService.incrementFailedDetection();
            long totalTime = System.currentTimeMillis() - startTime;
            processingTime.setTotalTimeMs(totalTime);
            QRCodeResult result = new QRCodeResult(false, "处理图片失败: " + e.getMessage());
            result.setProcessingTime(processingTime);
            return result;
        } finally {
            metricsService.stopTotalProcessingTimer(totalProcessingSample);
        }
    }
    
    /**
     * 直接从字节数组加载图片为灰度图像，避免BufferedImage转换
     * 优化版本：使用更高效的内存操作
     * 
     * @param imageBytes 图片字节数据
     * @param processingTime 处理时间对象，用于记录图片信息
     * @return 灰度图像，如果加载失败返回null
     */
    /**
     * 简化的直接加载方法：只进行必要的BufferedImage到GrayU8转换，不做任何预处理
     */
    private GrayU8 loadImageSimpleDirect(byte[] imageBytes, ProcessingTime processingTime) {
        BufferedImage bufferedImage = null;
        try {
            // 从字节数组创建BufferedImage
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            bufferedImage = ImageIO.read(inputStream);
            inputStream.close();
            
            if (bufferedImage == null) {
                return null;
            }
            
            // 记录图片信息
            processingTime.setImageWidth(bufferedImage.getWidth());
            processingTime.setImageHeight(bufferedImage.getHeight());
            
            // 直接转换为GrayU8，不做任何预处理
            GrayU8 grayImage = ConvertBufferedImage.convertFrom(bufferedImage, (GrayU8) null);
            
            return grayImage;
            
        } catch (Exception e) {
            LOG.error("简化图片加载失败: " + e.getMessage(), e);
            return null;
        } finally {
            // 及时释放BufferedImage资源
            if (bufferedImage != null) {
                bufferedImage.flush();
                bufferedImage = null;
            }
        }
    }
    
    /**
     * 从灰度图像中检测QR码
     * 
     * @param grayImage 灰度图像
     * @param processingTime 处理时间对象
     * @return 检测结果
     */
    private QRCodeResult detectQRCodesFromGrayImage(GrayU8 grayImage, ProcessingTime processingTime) {
        try {
            // 检测QR码
            long detectionStart = System.currentTimeMillis();
            Timer.Sample detectionSample = metricsService.startQrDetectionTimer();
            detector.process(grayImage);
            List<QrCode> detections = detector.getDetections();
            long detectionEnd = System.currentTimeMillis();
            metricsService.stopQrDetectionTimer(detectionSample);
            
            long detectionTime = detectionEnd - detectionStart;
            processingTime.setDetectionTimeMs(detectionTime);
            
            List<QRCodeInfo> qrCodeInfos = new ArrayList<>();
            
            for (QrCode qrCode : detections) {
                // 获取QR码内容
                String content = qrCode.message;
                
                // 获取边界框
                Polygon2D_F64 bounds = qrCode.bounds;
                List<Point> corners = new ArrayList<>();
                
                for (int i = 0; i < bounds.size(); i++) {
                    Point2D_F64 corner = bounds.get(i);
                    corners.add(new Point(corner.x, corner.y));
                }
                
                BoundingBox boundingBox = new BoundingBox(corners);
                
                // 计算置信度（基于QR码检测的质量）
                double confidence = calculateConfidence(qrCode);
                
                QRCodeInfo qrCodeInfo = new QRCodeInfo(content, boundingBox, confidence);
                qrCodeInfos.add(qrCodeInfo);
                
                LOG.info("检测到QR码: " + content + ", 位置: " + boundingBox.getX() + "," + boundingBox.getY());
            }
            
            return new QRCodeResult(true, qrCodeInfos, processingTime);
            
        } catch (Exception e) {
            LOG.error("检测QR码时出错", e);
            return new QRCodeResult(false, "检测QR码失败: " + e.getMessage());
        }
    }
    
    /**
     * 从BufferedImage中检测QR码（内部方法）
     * 
     * @param bufferedImage BufferedImage对象
     * @param processingTime 处理时间对象
     * @return 检测结果
     */
    private QRCodeResult detectQRCodesInternal(BufferedImage bufferedImage, ProcessingTime processingTime) {
        try {
            // 转换为灰度图像
            GrayU8 grayImage = ConvertBufferedImage.convertFrom(bufferedImage, (GrayU8) null);
            
            // 检测QR码
            long detectionStart = System.currentTimeMillis();
            Timer.Sample detectionSample = metricsService.startQrDetectionTimer();
            detector.process(grayImage);
            List<QrCode> detections = detector.getDetections();
            long detectionEnd = System.currentTimeMillis();
            metricsService.stopQrDetectionTimer(detectionSample);
            
            long detectionTime = detectionEnd - detectionStart;
            processingTime.setDetectionTimeMs(detectionTime);
            
            List<QRCodeInfo> qrCodeInfos = new ArrayList<>();
            
            for (QrCode qrCode : detections) {
                // 获取QR码内容
                String content = qrCode.message;
                
                // 获取边界框
                Polygon2D_F64 bounds = qrCode.bounds;
                List<Point> corners = new ArrayList<>();
                
                for (int i = 0; i < bounds.size(); i++) {
                    Point2D_F64 corner = bounds.get(i);
                    corners.add(new Point(corner.x, corner.y));
                }
                
                BoundingBox boundingBox = new BoundingBox(corners);
                
                // 计算置信度（基于QR码检测的质量）
                double confidence = calculateConfidence(qrCode);
                
                QRCodeInfo qrCodeInfo = new QRCodeInfo(content, boundingBox, confidence);
                qrCodeInfos.add(qrCodeInfo);
                
                LOG.info("检测到QR码: " + content + ", 位置: " + boundingBox.getX() + "," + boundingBox.getY());
            }
            
            return new QRCodeResult(true, qrCodeInfos, processingTime);
            
        } catch (Exception e) {
            LOG.error("检测QR码时出错", e);
            return new QRCodeResult(false, "检测QR码失败: " + e.getMessage());
        }
    }
    
    /**
     * 从BufferedImage中检测QR码（公共方法，保持向后兼容）
     * 
     * @param bufferedImage BufferedImage对象
     * @return 检测结果
     */
    public QRCodeResult detectQRCodes(BufferedImage bufferedImage) {
        ProcessingTime processingTime = new ProcessingTime();
        processingTime.setImageWidth(bufferedImage.getWidth());
        processingTime.setImageHeight(bufferedImage.getHeight());
        
        long startTime = System.currentTimeMillis();
        QRCodeResult result = detectQRCodesInternal(bufferedImage, processingTime);
        long totalTime = System.currentTimeMillis() - startTime;
        processingTime.setTotalTimeMs(totalTime);
        result.setProcessingTime(processingTime);
        
        return result;
    }
    
    /**
     * 计算QR码检测的置信度
     * 
     * @param qrCode QR码对象
     * @return 置信度值 (0.0 - 1.0)
     */
    private double calculateConfidence(QrCode qrCode) {
        // 基于QR码的版本、纠错级别等因素计算置信度
        // 这里简化为基于边界框的完整性
        if (qrCode.bounds != null && qrCode.bounds.size() == 4) {
            return 0.95; // 高置信度
        } else {
            return 0.7; // 中等置信度
        }
    }
}

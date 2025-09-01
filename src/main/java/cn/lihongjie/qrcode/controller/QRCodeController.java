package cn.lihongjie.qrcode.controller;

import cn.lihongjie.qrcode.model.QRCodeResult;
import cn.lihongjie.qrcode.service.QRCodeDetectionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jboss.resteasy.reactive.RestForm;

import java.io.IOException;
import java.nio.file.Files;

/**
 * QR码检测REST API控制器
 */
@Path("/api/qrcode")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QRCodeController {
    
    private static final Logger LOG = Logger.getLogger(QRCodeController.class);
    
    @Inject
    QRCodeDetectionService qrCodeDetectionService;
    
    /**
     * 上传图片并检测QR码
     * 
     * @param file 上传的图片文件
     * @return QR码检测结果
     */
    @POST
    @Path("/detect")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response detectQRCodes(@RestForm("file") FileUpload file) {
        
        if (file == null || file.uploadedFile() == null) {
            QRCodeResult result = new QRCodeResult(false, "请上传图片文件");
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        
        try {
            // 验证文件类型
            String contentType = file.contentType();
            if (!isValidImageType(contentType)) {
                QRCodeResult result = new QRCodeResult(false, "不支持的文件类型，请上传JPG、PNG或GIF图片");
                return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
            }
            
            // 读取文件内容
            byte[] imageBytes = Files.readAllBytes(file.uploadedFile());
            
            // 检测QR码
            QRCodeResult result = qrCodeDetectionService.detectQRCodes(imageBytes);
            
            if (result.isSuccess()) {
                LOG.info("成功检测到 " + result.getCount() + " 个QR码");
                return Response.ok(result).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
            }
            
        } catch (IOException e) {
            LOG.error("读取上传文件时出错", e);
            QRCodeResult result = new QRCodeResult(false, "读取文件失败: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        } catch (Exception e) {
            LOG.error("处理请求时出错", e);
            QRCodeResult result = new QRCodeResult(false, "处理请求失败: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
    }
    
    /**
     * 健康检查接口
     * 
     * @return 服务状态
     */
    @GET
    @Path("/health")
    public Response health() {
        return Response.ok("{\"status\":\"OK\",\"service\":\"QR Code Detection Service\"}").build();
    }
    
    /**
     * 验证是否为有效的图片类型
     * 
     * @param contentType MIME类型
     * @return 是否为有效的图片类型
     */
    private boolean isValidImageType(String contentType) {
        if (contentType == null) {
            return false;
        }
        
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/bmp") ||
               contentType.equals("image/webp");
    }
}

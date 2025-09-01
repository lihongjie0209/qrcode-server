package cn.lihongjie.qrcode.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * 前端页面控制器
 */
@Path("/")
public class WebController {
    
    @Inject
    Template index;
    
    /**
     * 首页 - QR码检测测试页面
     * 
     * @return 首页模板
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        return index.data("title", "QR码检测服务");
    }
}

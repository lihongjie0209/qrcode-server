package cn.lihongjie.qrcode.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Gauge;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Metrics控制器，提供指标查看接口
 */
@Path("/metrics")
public class MetricsController {
    
    @Inject
    MeterRegistry meterRegistry;
    
    @Inject
    Template metrics;
    
    /**
     * 获取所有metrics的JSON格式
     */
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMetricsJson() {
        Map<String, Object> metricsData = new HashMap<>();
        
        // 计数器数据
        Map<String, Double> counters = new HashMap<>();
        // 计时器数据
        Map<String, Map<String, Double>> timers = new HashMap<>();
        // 仪表数据
        Map<String, Double> gauges = new HashMap<>();
        
        for (Meter meter : meterRegistry.getMeters()) {
            if (meter instanceof Counter counter) {
                counters.put(meter.getId().getName(), counter.count());
            } else if (meter instanceof Timer timer) {
                Map<String, Double> timerData = new HashMap<>();
                timerData.put("count", (double) timer.count());
                timerData.put("totalTime", timer.totalTime(TimeUnit.MILLISECONDS));
                timerData.put("mean", timer.mean(TimeUnit.MILLISECONDS));
                timerData.put("max", timer.max(TimeUnit.MILLISECONDS));
                timers.put(meter.getId().getName(), timerData);
            } else if (meter instanceof Gauge gauge) {
                gauges.put(meter.getId().getName(), gauge.value());
            }
        }
        
        metricsData.put("counters", counters);
        metricsData.put("timers", timers);
        metricsData.put("gauges", gauges);
        metricsData.put("timestamp", System.currentTimeMillis());
        
        return Response.ok(metricsData).build();
    }
    
    /**
     * Metrics仪表板页面
     */
    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance dashboard() {
        return metrics.data("title", "QR码检测服务 - 监控仪表板");
    }
    
    /**
     * 获取QR码相关的metrics
     */
    @GET
    @Path("/qrcode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQRCodeMetrics() {
        Map<String, Object> qrMetrics = new HashMap<>();
        
        for (Meter meter : meterRegistry.getMeters()) {
            String name = meter.getId().getName();
            if (name.startsWith("qrcode.")) {
                if (meter instanceof Counter counter) {
                    qrMetrics.put(name, counter.count());
                } else if (meter instanceof Timer timer) {
                    Map<String, Object> timerData = new HashMap<>();
                    timerData.put("count", timer.count());
                    timerData.put("totalTime", timer.totalTime(TimeUnit.MILLISECONDS));
                    timerData.put("mean", timer.mean(TimeUnit.MILLISECONDS));
                    timerData.put("max", timer.max(TimeUnit.MILLISECONDS));
                    qrMetrics.put(name, timerData);
                } else if (meter instanceof Gauge gauge) {
                    qrMetrics.put(name, gauge.value());
                }
            }
        }
        
        return Response.ok(qrMetrics).build();
    }
    
    /**
     * 重置所有计数器（仅用于测试）
     */
    @POST
    @Path("/reset")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetMetrics() {
        // 注意：Micrometer的Counter不支持重置，这里只是返回操作结果
        Map<String, String> result = new HashMap<>();
        result.put("message", "Metrics reset operation completed (note: counters cannot be reset in Micrometer)");
        result.put("timestamp", new Date().toString());
        
        return Response.ok(result).build();
    }
}

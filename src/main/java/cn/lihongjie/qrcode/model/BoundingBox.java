package cn.lihongjie.qrcode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 边界框，定义QR码在图片中的位置
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoundingBox {
    
    private double x;
    private double y;
    private double width;
    private double height;
    private List<Point> corners;
    
    public BoundingBox() {}
    
    public BoundingBox(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public BoundingBox(List<Point> corners) {
        this.corners = corners;
        calculateBounds();
    }
    
    private void calculateBounds() {
        if (corners == null || corners.size() < 4) {
            return;
        }
        
        double minX = corners.stream().mapToDouble(Point::getX).min().orElse(0);
        double maxX = corners.stream().mapToDouble(Point::getX).max().orElse(0);
        double minY = corners.stream().mapToDouble(Point::getY).min().orElse(0);
        double maxY = corners.stream().mapToDouble(Point::getY).max().orElse(0);
        
        this.x = minX;
        this.y = minY;
        this.width = maxX - minX;
        this.height = maxY - minY;
    }
    
    // Getters and setters
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public List<Point> getCorners() {
        return corners;
    }
    
    public void setCorners(List<Point> corners) {
        this.corners = corners;
        calculateBounds();
    }
}

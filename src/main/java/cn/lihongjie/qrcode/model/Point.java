package cn.lihongjie.qrcode.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 二维点坐标
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Point {
    
    private double x;
    private double y;
    
    public Point() {}
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
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
    
    @Override
    public String toString() {
        return String.format("Point(%.2f, %.2f)", x, y);
    }
}

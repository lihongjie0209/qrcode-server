#!/usr/bin/env python3
"""
生成BoofCV GraalVM原生映像反射配置
"""
import json
import os
from pathlib import Path

def generate_boofcv_reflection_config():
    """生成BoofCV相关类的反射配置"""
    
    # BoofCV核心包和类
    boofcv_packages = [
        # 核心结构
        "boofcv.struct.image",
        "boofcv.struct.geo",
        "boofcv.struct",
        
        # QR码相关
        "boofcv.alg.fiducial.qrcode",
        "boofcv.factory.fiducial",
        
        # 形状检测
        "boofcv.alg.shapes.polygon",
        "boofcv.alg.shapes.polyline.splitmerge",
        "boofcv.alg.shapes.corner",
        "boofcv.alg.shapes.edge",
        
        # 图像处理
        "boofcv.alg.filter.binary",
        "boofcv.alg.filter.blur",
        "boofcv.alg.misc",
        "boofcv.alg.enhance",
        
        # IO
        "boofcv.io.image",
        
        # 算法
        "boofcv.alg.segmentation",
        "boofcv.alg.interpolate",
        "boofcv.alg.transform.pyramid",
        
        # 数据结构
        "boofcv.struct.border",
        "boofcv.struct.convolve",
        "boofcv.struct.distort",
        "boofcv.struct.feature",
        "boofcv.struct.pyramid",
    ]
    
    # 常用的BoofCV类
    common_classes = [
        # 图像类型
        "boofcv.struct.image.GrayU8",
        "boofcv.struct.image.GrayF32",
        "boofcv.struct.image.GrayS16",
        "boofcv.struct.image.GrayS32",
        "boofcv.struct.image.GrayF64",
        "boofcv.struct.image.ImageGray",
        "boofcv.struct.image.ImageBase",
        "boofcv.struct.image.ImageInterleaved",
        "boofcv.struct.image.Planar",
        
        # 几何类型
        "boofcv.struct.geo.Point2D_F64",
        "boofcv.struct.geo.Point2D_F32",
        "boofcv.struct.geo.Point2D_I32",
        "boofcv.struct.geo.Point3D_F64",
        "boofcv.struct.geo.PointIndex2D_F64",
        
        # QR码相关
        "boofcv.alg.fiducial.qrcode.QrCode",
        "boofcv.alg.fiducial.qrcode.QrCodeDetector",
        "boofcv.alg.fiducial.qrcode.QrCodePositionPatternDetector",
        "boofcv.alg.fiducial.qrcode.QrCodeAlignmentPatternLocator",
        "boofcv.alg.fiducial.qrcode.QrCodeBinaryGridReader",
        "boofcv.alg.fiducial.qrcode.QrCodeCodeWordLocations",
        "boofcv.alg.fiducial.qrcode.QrCodeDecoderImage",
        "boofcv.alg.fiducial.qrcode.QrCodeMaskPattern",
        "boofcv.factory.fiducial.ConfigQrCode",
        "boofcv.factory.fiducial.FactoryFiducial",
        
        # 形状检测
        "boofcv.alg.shapes.polyline.splitmerge.PolylineSplitMerge",
        "boofcv.alg.shapes.polyline.splitmerge.PolylineSplitMerge$Corner",
        "boofcv.alg.shapes.polygon.BinaryPolygonDetector",
        "boofcv.alg.shapes.polygon.DetectPolygonBinaryGrayRefine",
        "boofcv.alg.shapes.polygon.DetectPolygonFromContour",
        
        # 图像处理
        "boofcv.alg.filter.binary.BinaryImageOps",
        "boofcv.alg.filter.binary.ThresholdImageOps",
        "boofcv.alg.filter.binary.Contour",
        "boofcv.alg.filter.binary.LinearContourLabelChang2004",
        "boofcv.alg.misc.GImageMiscOps",
        "boofcv.io.image.ConvertBufferedImage",
        
        # 连接规则和其他结构
        "boofcv.struct.ConnectRule",
        "boofcv.struct.ConfigLength",
        "boofcv.struct.Configuration",
        
        # 数组类型
        "boofcv.struct.image.GrayU8[]",
        "boofcv.struct.image.GrayF32[]",
        "boofcv.struct.geo.Point2D_F64[]",
        "boofcv.alg.shapes.polyline.splitmerge.PolylineSplitMerge$Corner[]",
    ]
    
    # 生成反射配置
    reflection_config = []
    
    for class_name in common_classes:
        reflection_config.append({
            "name": class_name,
            "allDeclaredConstructors": True,
            "allDeclaredMethods": True,
            "allDeclaredFields": True,
            "allPublicConstructors": True,
            "allPublicMethods": True,
            "allPublicFields": True,
            "unsafeAllocated": True
        })
    
    # 添加一些通用的模式匹配
    pattern_classes = [
        "boofcv.alg.**",
        "boofcv.struct.**",
        "boofcv.factory.**",
        "boofcv.io.**",
    ]
    
    for pattern in pattern_classes:
        reflection_config.append({
            "name": pattern,
            "allDeclaredConstructors": True,
            "allDeclaredMethods": True,
            "allDeclaredFields": True,
            "unsafeAllocated": True
        })
    
    return reflection_config

def generate_resource_config():
    """生成资源配置"""
    return {
        "resources": {
            "includes": [
                {"pattern": ".*\\.properties$"},
                {"pattern": "META-INF/.*"},
                {"pattern": "boofcv/.*"},
                {"pattern": ".*\\.xml$"},
                {"pattern": ".*\\.txt$"},
                {"pattern": ".*\\.dat$"},
            ]
        }
    }

def main():
    """主函数"""
    # 确保目录存在
    config_dir = Path("src/main/resources/META-INF/native-image")
    config_dir.mkdir(parents=True, exist_ok=True)
    
    # 生成反射配置
    reflection_config = generate_boofcv_reflection_config()
    with open(config_dir / "reflect-config.json", "w", encoding="utf-8") as f:
        json.dump(reflection_config, f, indent=2, ensure_ascii=False)
    
    # 生成资源配置
    resource_config = generate_resource_config()
    with open(config_dir / "resource-config.json", "w", encoding="utf-8") as f:
        json.dump(resource_config, f, indent=2, ensure_ascii=False)
    
    # 创建空的其他配置文件
    for config_file in ["jni-config.json", "proxy-config.json", "serialization-config.json"]:
        config_path = config_dir / config_file
        if not config_path.exists():
            with open(config_path, "w", encoding="utf-8") as f:
                json.dump([], f, indent=2)
    
    print("✅ BoofCV GraalVM 原生映像配置文件已生成")
    print(f"📁 配置目录: {config_dir}")
    print("📋 生成的文件:")
    for file in config_dir.glob("*.json"):
        print(f"   - {file.name}")

if __name__ == "__main__":
    main()

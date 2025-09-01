package cn.lihongjie.qrcode.config;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * BoofCV原生镜像反射配置
 * 
 * 这个类用于在GraalVM原生镜像编译时注册BoofCV相关的类进行反射。
 * 
 * @author lihongjie
 */
@RegisterForReflection(targets = {
    // 核心图像类型
    boofcv.struct.image.GrayU8.class,
    boofcv.struct.image.GrayF32.class,
    boofcv.struct.image.ImageGray.class,
    boofcv.struct.image.ImageBase.class,
    
    // QR码检测相关
    boofcv.alg.fiducial.qrcode.QrCode.class,
    boofcv.factory.fiducial.ConfigQrCode.class,
    boofcv.factory.fiducial.FactoryFiducial.class,
    
    // 形状检测
    boofcv.alg.shapes.polyline.splitmerge.PolylineSplitMerge.class,
    
    // 图像处理
    boofcv.alg.filter.binary.BinaryImageOps.class,
    boofcv.alg.filter.binary.ThresholdImageOps.class,
    boofcv.io.image.ConvertBufferedImage.class,
    
    // 连接规则
    boofcv.struct.ConnectRule.class,
})
public class BoofCVNativeConfig {
    // 这个类只用于注册反射，不需要实现
}

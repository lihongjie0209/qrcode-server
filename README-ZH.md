# QR码检测服务

这是一个基于 Quarkus 和 BoofCV 的 QR码检测服务，支持从图片中检测多个QR码，并提供二维码的内容和位置信息。

## 功能特性

- 🔍 **多QR码检测**: 支持同时检测图片中的多个QR码
- 📍 **位置信息**: 提供每个QR码的精确位置坐标和边界框
- 🎯 **置信度评估**: 为每个检测结果提供置信度分数
- 🌐 **Web界面**: 提供友好的前端测试界面，支持拖拽上传
- 🏷️ **可视化标注**: 在上传的图片上自动标注检测到的QR码区域
- � **监控指标**: 集成Micrometer，提供详细的性能监控和指标收集
- 📈 **实时仪表板**: 内置监控仪表板，实时显示服务运行状态和性能指标
- �🚀 **高性能**: 基于 Quarkus 框架，启动快速，资源占用少

## 技术栈

- **后端框架**: Quarkus 3.26.1
- **QR码检测**: BoofCV 1.1.6
- **监控指标**: Micrometer + Prometheus
- **Web界面**: Bootstrap 5 + 原生 JavaScript
- **构建工具**: Maven
- **Java版本**: 21

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.8+

### 2. 启动应用

```bash
# 开发模式运行（支持热重载）
./mvnw quarkus:dev

# 或者在 Windows 上
mvnw.cmd quarkus:dev
```

### 3. 访问应用

- **Web界面**: http://localhost:8080
- **监控仪表板**: http://localhost:8080/metrics/dashboard
- **Prometheus指标**: http://localhost:8080/metrics/prometheus
- **API文档**: http://localhost:8080/q/dev-ui （开发模式下）

## API 接口

### 检测QR码

**POST** `/api/qrcode/detect`

**请求格式**: `multipart/form-data`

**参数**:
- `file`: 图片文件 (支持 JPG, PNG, GIF, BMP, WebP)

**响应字段说明**:
- `success`: 检测是否成功
- `count`: 检测到的QR码数量
- `qrCodes`: QR码信息数组
  - `content`: QR码内容
  - `confidence`: 检测置信度 (0.0-1.0)
  - `boundingBox`: 边界框信息
    - `x, y`: 左上角坐标
    - `width, height`: 宽度和高度
    - `corners`: 四个角点坐标
- `processingTime`: 处理时间信息
  - `totalTimeMs`: 总处理时间(毫秒)
  - `imageLoadTimeMs`: 图片加载时间(毫秒)
  - `detectionTimeMs`: QR码检测时间(毫秒)
  - `fileSizeBytes`: 文件大小(字节)
  - `imageWidth`: 图片宽度(像素)
  - `imageHeight`: 图片高度(像素)

**响应示例**:
```json
{
  "success": true,
  "count": 2,
  "qrCodes": [
    {
      "content": "https://www.example.com",
      "confidence": 0.95,
      "boundingBox": {
        "x": 100.5,
        "y": 150.2,
        "width": 200.0,
        "height": 200.0,
        "corners": [
          {"x": 100.5, "y": 150.2},
          {"x": 300.5, "y": 150.2},
          {"x": 300.5, "y": 350.2},
          {"x": 100.5, "y": 350.2}
        ]
      }
    }
  ],
  "processingTime": {
    "totalTimeMs": 156,
    "imageLoadTimeMs": 45,
    "detectionTimeMs": 89,
    "fileSizeBytes": 524288,
    "imageWidth": 1920,
    "imageHeight": 1080
  }
}
```

### 健康检查

**GET** `/api/qrcode/health`

**响应**:
```json
{
  "status": "OK",
  "service": "QR Code Detection Service"
}
```

### 监控指标

**GET** `/metrics/qrcode`

**响应**: 获取QR码检测相关的指标
```json
{
  "qrcode.detection.requests.total": 150,
  "qrcode.detection.success.total": 145,
  "qrcode.detection.failed.total": 5,
  "qrcode.detected.total": 298,
  "qrcode.processing.total.duration": {
    "count": 145,
    "totalTime": 12500.0,
    "mean": 86.2
  },
  "qrcode.image.load.duration": {
    "count": 145,
    "totalTime": 3200.0,
    "mean": 22.1
  },
  "qrcode.detection.duration": {
    "count": 145,
    "totalTime": 8900.0,
    "mean": 61.4
  }
}
```

**GET** `/metrics/dashboard`

**响应**: 监控仪表板HTML页面

**GET** `/metrics/prometheus`

**响应**: Prometheus格式的指标数据

## 使用示例

### 1. 通过Web界面

1. 访问 http://localhost:8080
2. 拖拽图片到上传区域或点击选择文件
3. 查看检测结果和位置标注

### 2. 通过API调用

```bash
# 使用 curl 上传图片检测QR码
curl -X POST \
  http://localhost:8080/api/qrcode/detect \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/your/image.jpg"
```

### 3. 使用 JavaScript

```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

fetch('/api/qrcode/detect', {
    method: 'POST',
    body: formData
})
.then(response => response.json())
.then(data => {
    console.log('检测结果:', data);
    if (data.success) {
        // 显示QR码信息
        data.qrCodes.forEach(qr => {
            console.log(`内容: ${qr.content}`);
            console.log(`位置: (${qr.boundingBox.x}, ${qr.boundingBox.y})`);
            console.log(`置信度: ${qr.confidence}`);
        });
        
        // 显示性能信息
        if (data.processingTime) {
            const perf = data.processingTime;
            console.log(`总耗时: ${perf.totalTimeMs}ms`);
            console.log(`图片加载: ${perf.imageLoadTimeMs}ms`);
            console.log(`检测时间: ${perf.detectionTimeMs}ms`);
            console.log(`文件大小: ${(perf.fileSizeBytes / 1024).toFixed(1)}KB`);
            console.log(`图片尺寸: ${perf.imageWidth}×${perf.imageHeight}`);
            
            // 计算处理速度
            const speed = (perf.imageWidth * perf.imageHeight / perf.totalTimeMs).toFixed(0);
            console.log(`处理速度: ${speed} 像素/ms`);
        }
    }
});
```

## 构建和部署

### 开发模式

```bash
./mvnw quarkus:dev
```

### 构建 JAR 包

```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

### 构建 Native 镜像

```bash
./mvnw package -Dnative
./target/qrcode-server-1.0-SNAPSHOT-runner
```

### Docker 构建

```bash
# 构建 JVM 版本
./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t qrcode-server:jvm .

# 构建 Native 版本
./mvnw package -Dnative
docker build -f src/main/docker/Dockerfile.native -t qrcode-server:native .
```

## 配置说明

主要配置项在 `src/main/resources/application.properties` 中：

```properties
# HTTP端口
quarkus.http.port=8080

# 文件上传大小限制
quarkus.http.limits.max-body-size=50M
quarkus.http.body.multipart.max-file-size=50M

# 日志级别
quarkus.log.level=INFO
quarkus.log.category."cn.lihongjie".level=DEBUG
```

## 性能优化建议

1. **内存设置**: 根据需要调整 JVM 内存参数
2. **并发处理**: 服务支持并发请求处理
3. **图片尺寸**: 建议上传图片不超过 10MB，分辨率适中即可
4. **Native 模式**: 生产环境建议使用 Native 镜像以获得更好的启动性能

### 性能基准参考

基于不同图片尺寸的处理时间参考：

| 图片尺寸 | 文件大小 | 平均总耗时 | 图片加载 | QR检测 |
|---------|---------|-----------|---------|-------|
| 640×480 | 100KB | 50-80ms | 15-25ms | 25-45ms |
| 1280×720 | 300KB | 80-120ms | 25-40ms | 45-70ms |
| 1920×1080 | 500KB | 120-180ms | 40-60ms | 70-110ms |
| 2560×1440 | 800KB | 180-250ms | 60-90ms | 110-150ms |

*注：实际性能取决于硬件配置、图片复杂度和QR码数量*

### 性能监控

- 通过 `/metrics/dashboard` 查看实时性能指标
- 通过 `/metrics/prometheus` 获取Prometheus格式的监控数据
- API响应中的 `processingTime` 字段提供详细的时间分解

## 常见问题

### Q: 为什么检测不到QR码？
A: 请确保：
- 图片清晰度足够
- QR码没有被遮挡或变形严重
- 图片格式正确

### Q: 如何提高检测精度？
A: 建议：
- 使用高质量的图片
- 确保QR码与背景有足够的对比度
- 避免图片模糊或过度压缩

### Q: 支持的最大图片尺寸？
A: 默认支持最大 50MB 的图片文件，可在配置文件中调整。

## 许可证

MIT License

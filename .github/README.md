# GitHub Actions Setup for Docker Build and Push

## Required Secrets

为了使GitHub Actions能够推送Docker镜像到Docker Hub并部署到GCP Cloud Run，您需要在GitHub仓库中设置以下secrets：

### 设置步骤：

1. 进入您的GitHub仓库页面
2. 点击 **Settings** 标签
3. 在左侧菜单中选择 **Secrets and variables** → **Actions**
4. 点击 **New repository secret** 按钮
5. 添加以下secrets：

#### Docker Hub Secrets

##### DOCKER_USERNAME
- **Name**: `DOCKER_USERNAME`
- **Value**: `lihongjie0209` (您的Docker Hub用户名)

##### DOCKER_PASSWORD
- **Name**: `DOCKER_PASSWORD`
- **Value**: 您的Docker Hub密码或访问令牌（推荐使用访问令牌）

#### GCP Secrets

##### GCP_SA_KEY
- **Name**: `GCP_SA_KEY`
- **Value**: GCP服务账号的JSON密钥

### 获取Docker Hub访问令牌（推荐）：

1. 登录 [Docker Hub](https://hub.docker.com/)
2. 点击右上角的用户名，选择 **Account Settings**
3. 选择 **Security** 标签
4. 点击 **New Access Token**
5. 输入描述（如：GitHub Actions）
6. 选择权限（读写权限）
7. 点击 **Generate**
8. 复制生成的令牌并将其作为 `DOCKER_PASSWORD` secret的值

### 设置GCP服务账号：

1. 在 [Google Cloud Console](https://console.cloud.google.com/) 中创建一个服务账号
2. 为服务账号分配以下权限：
   - Cloud Run Admin
   - Service Account User
   - Storage Admin (如果需要访问存储)
3. 创建并下载服务账号的JSON密钥文件
4. 将JSON文件的完整内容作为 `GCP_SA_KEY` secret的值

## Repository Variables

您还可以设置以下repository variables来自定义部署配置：

1. 在GitHub仓库中，进入 **Settings** → **Secrets and variables** → **Actions**
2. 点击 **Variables** 标签
3. 添加以下变量（可选）：

### Cloud Run配置变量

- `CLOUD_RUN_SERVICE_NAME`: 服务名称（默认：qrcode-server）
- `GCP_REGION`: 部署区域（默认：asia-east2）
- `CLOUD_RUN_MEMORY`: 内存限制（默认：1Gi）
- `CLOUD_RUN_CPU`: CPU数量（默认：1）
- `CLOUD_RUN_CONCURRENCY`: 并发数（默认：80）
- `CLOUD_RUN_MAX_INSTANCES`: 最大实例数（默认：10）
- `CLOUD_RUN_MIN_INSTANCES`: 最小实例数（默认：0）
- `CLOUD_RUN_TIMEOUT`: 请求超时时间（默认：300秒）
- `QUARKUS_LOG_LEVEL`: 日志级别（默认：INFO）

## Workflow 功能

这个GitHub Actions workflow包含以下功能：

### 触发条件
- 推送到 `main` 或 `master` 分支：构建并推送镜像
- 推送标签（如 `v1.0.0`）：构建、推送镜像并部署到Cloud Run
- 针对 `main` 或 `master` 分支的Pull Request：仅构建（不推送）

### 构建步骤
1. **代码检出**: 获取最新代码
2. **Java 21设置**: 安装Temurin JDK 21
3. **Maven缓存**: 缓存Maven依赖以加速构建
4. **GraalVM安装**: 安装GraalVM用于原生编译
5. **原生可执行文件构建**: 使用 `./mvnw package -Dnative` 构建
6. **Docker镜像构建和推送**: 使用Dockerfile.native构建镜像并推送到Docker Hub

### Cloud Run部署步骤（仅在打标签时触发）
1. **GCP身份验证**: 使用服务账号密钥
2. **版本号提取**: 从Git标签提取版本号
3. **Cloud Run部署**: 部署最新构建的镜像
4. **服务URL获取**: 获取部署后的服务URL
5. **部署总结**: 生成详细的部署报告

### 镜像标签策略
- `latest`: 主分支的最新提交
- `<branch-name>`: 分支名称
- `v<version>`: 语义化版本标签
- `<major>.<minor>`: 主版本.次版本

### 推送的镜像
镜像将推送到: `docker.io/lihongjie0209/qrcode-server`

### Cloud Run部署
- 服务名：qrcode-server（可通过变量自定义）
- 端口：8080（Quarkus默认端口）
- 区域：asia-east2（可通过变量自定义）
- 访问权限：允许未经身份验证的调用

## 本地测试

在本地测试Docker镜像构建：

```bash
# 构建原生可执行文件
./mvnw package -Dnative

# 构建Docker镜像
docker build -f src/main/docker/Dockerfile.native -t lihongjie0209/qrcode-server:local .

# 运行容器
docker run -i --rm -p 8080:8080 lihongjie0209/qrcode-server:local
```

## 部署测试

部署成功后，您可以使用以下命令测试服务：

```bash
# 替换为您的Cloud Run服务URL
SERVICE_URL="https://your-service-url"

# 健康检查
curl $SERVICE_URL/q/health

# QR码API健康检查
curl $SERVICE_URL/api/qrcode/health

# 测试QR码检测（需要上传包含QR码的图片）
curl -X POST -F "file=@path/to/your/qrcode-image.jpg" \
     "$SERVICE_URL/api/qrcode/detect"

# 获取Prometheus指标
curl $SERVICE_URL/q/metrics
```

## 注意事项

1. **GraalVM原生编译**: 需要足够的内存和时间，GitHub Actions runner有6GB内存限制
2. **平台支持**: 目前只构建 `linux/amd64` 平台的镜像
3. **Cloud Run部署**: 仅在推送版本标签时触发
4. **权限设置**: 确保GCP服务账号有足够的权限进行Cloud Run部署
5. **成本控制**: Cloud Run按使用量计费，建议合理设置并发和实例数限制

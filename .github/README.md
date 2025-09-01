# GitHub Actions Setup for Docker Build and Push

## Required Secrets

为了使GitHub Actions能够推送Docker镜像到Docker Hub，您需要在GitHub仓库中设置以下secrets：

### 设置步骤：

1. 进入您的GitHub仓库页面
2. 点击 **Settings** 标签
3. 在左侧菜单中选择 **Secrets and variables** → **Actions**
4. 点击 **New repository secret** 按钮
5. 添加以下secrets：

#### DOCKER_USERNAME
- **Name**: `DOCKER_USERNAME`
- **Value**: `lihongjie0209` (您的Docker Hub用户名)

#### DOCKER_PASSWORD
- **Name**: `DOCKER_PASSWORD`
- **Value**: 您的Docker Hub密码或访问令牌（推荐使用访问令牌）

### 获取Docker Hub访问令牌（推荐）：

1. 登录 [Docker Hub](https://hub.docker.com/)
2. 点击右上角的用户名，选择 **Account Settings**
3. 选择 **Security** 标签
4. 点击 **New Access Token**
5. 输入描述（如：GitHub Actions）
6. 选择权限（读写权限）
7. 点击 **Generate**
8. 复制生成的令牌并将其作为 `DOCKER_PASSWORD` secret的值

## Workflow 功能

这个GitHub Actions workflow包含以下功能：

### 触发条件
- 推送到 `main` 或 `master` 分支
- 推送标签（如 `v1.0.0`）
- 针对 `main` 或 `master` 分支的Pull Request

### 构建步骤
1. **代码检出**: 获取最新代码
2. **Java 21设置**: 安装Temurin JDK 21
3. **Maven缓存**: 缓存Maven依赖以加速构建
4. **GraalVM安装**: 安装GraalVM用于原生编译
5. **原生可执行文件构建**: 使用 `./mvnw package -Dnative` 构建
6. **Docker镜像构建和推送**: 使用Dockerfile.native构建镜像并推送到Docker Hub
7. **安全扫描**: 使用Trivy扫描镜像漏洞

### 镜像标签策略
- `latest`: 主分支的最新提交
- `<branch-name>`: 分支名称
- `v<version>`: 语义化版本标签
- `<major>.<minor>`: 主版本.次版本

### 推送的镜像
镜像将推送到: `docker.io/lihongjie0209/qrcode-server`

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

## 注意事项

1. **GraalVM原生编译**: 需要足够的内存和时间，GitHub Actions runner有6GB内存限制
2. **平台支持**: 目前只构建 `linux/amd64` 平台的镜像
3. **安全扫描**: Trivy会扫描构建的镜像并将结果上传到GitHub Security标签
4. **缓存**: 使用GitHub Actions缓存来加速Docker构建

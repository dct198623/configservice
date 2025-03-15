# Config Service

## 概述
採用 **Spring Cloud Config Server**，提供**集中式管理與動態更新**微服務配置，透過 Git 儲存，支持版本控制與歷史追蹤。

## 快速入門

### 微服務訪問配置
- URL 格式：`http://localhost:8888/{application}/{profile}`
- 範例：`http://127.0.0.1:8888/eurekaservice/dev`

### 微服務整合步驟
1. **添加依賴**：
   ```kotlin
   dependencies {
       implementation("org.springframework.cloud:spring-cloud-starter-config")
       implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
   }
   ```

2. **配置 bootstrap.yml**：
   ```yaml
   spring:
     cloud:
       config:
         name: ${服務名稱} # 例如：eurekaservice
         profile: dev # 環境設定：dev、test、prod 等
         uri: http://localhost:8888/ # 配置服務的 URI
         fail-fast: true # 如果連線失敗立即報錯
   ```

## 部署指南

### 自動部署 (GitHub Actions)
```bash
git checkout main
git pull --rebase origin main
git tag -a v0.0.1 -m "v0.0.1"
git push origin --tags
```

### 手動部署
1. **建立資料夾**：
   ```shell
   sudo mkdir -p /opt/tata/configservice
   ```

2. **放置 JAR 文件**：將 configservice.jar 放入 /opt/tata/configservice

3. **建立 Dockerfile**：
   ```shell
   sudo touch /opt/tata/configservice/Dockerfile
   sudo chown -R ubuntu:ubuntu /opt/tata/configservice/Dockerfile
   ```

4. **建構與啟動**：
   ```shell
   cd /opt/tata/configservice
   docker build --no-cache -t configservice .
   
   # 啟動容器（含環境變數設定）
   docker run -di --name=configservice \
     --network tata-network \
     -p 8888:8888 \
     -e SERVER_HOST=127.0.0.1 \
     -e SERVER_PORT=8888 \
     -e SECURITY_USERNAME=admin \
     -e SECURITY_PASSWORD=password \
     -e SPRING_PROFILES_ACTIVE=dev \
     configservice
   ```

5. **確認狀態**：
   ```shell
   docker logs -f --tail 1000 configservice
   ```

### 其他服務啟動命令

#### Eureka Service
```shell
# 啟動容器
docker run -d --name=eurekaservice --network tata-network -p 8761:8761 \
  -e SERVER_HOST=127.0.0.1 \
  -e SERVER_PORT=8761 \
  -e SECURITY_USERNAME=admin \
  -e SECURITY_PASSWORD=password \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e CONFIG_SERVER_USERNAME=admin \
  -e CONFIG_SERVER_PASSWORD=password \
  -e CONFIG_SERVER_URI=http://configservice:8888 \
  eurekaservice
```

#### API Gateway
```shell
# 啟動容器
docker run -d --name=gatewayservice --network tata-network -p 8080:8080 \
  -e SERVER_HOST=127.0.0.1 \
  -e SERVER_PORT=8080 \
  -e SECURITY_USERNAME=admin \
  -e SECURITY_PASSWORD=password \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e CONFIG_SERVER_USERNAME=admin \
  -e CONFIG_SERVER_PASSWORD=password \
  -e CONFIG_SERVER_URI=http://configservice:8888 \
  -e EUREKA_SERVER_HOST=eurekaservice \
  -e EUREKA_SERVER_PORT=8761 \
  gatewayservice
```

## 版本管理
- **MAJOR**: 不兼容的重大變更（例：1.0.0 → 2.0.0）
- **MINOR**: 新增功能，向下相容（例：1.1.0 → 1.2.0）
- **PATCH**: 修正錯誤，無功能變化（例：1.2.1 → 1.2.2）

## 參考資源
- [Spring Cloud Config 文檔](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)
- 服務主要配置文件：[application.yml](src%2Fmain%2Fresources%2Fapplication.yml)
- Docker 範例：[doc/Dockerfile](docs/docker/configservice/Dockerfile)
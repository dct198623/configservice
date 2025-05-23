# Config Service

![Spring Boot Version](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)
![Spring Cloud Version](https://img.shields.io/badge/Spring%20Cloud-2024.0.0-blue.svg)
![Java Version](https://img.shields.io/badge/Java-21-orange.svg)
![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)

## 概述

Config Service 是基於 Spring Cloud Config Server 的配置中心，提供微服務架構中的集中式配置管理解決方案。主要特點包括：

- **集中管理配置**：統一存儲所有微服務的配置設定
- **動態更新**：支持配置的即時更新，無需重啟服務
- **版本控制**：通過 Git 儲存配置，支持版本控制與變更歷史追蹤
- **環境隔離**：支持 dev、test、prod 等多環境配置隔離
- **安全保護**：整合 Spring Security 確保配置安全存取

## 技術堆疊

1. **框架**: Spring Boot 3.4.3
2. **配置中心**: Spring Cloud Config Server 2024.0.0
3. **Java 版本**: Java 21
4. **安全框架**: Spring Security
5. **監控**: Spring Boot Actuator
6. **構建工具**: Gradle (Kotlin DSL)
7. **版本控制**: 自動從 Git Tag 獲取版本號

## 系統架構

### 1. 訪問配置

透過 RESTful API 存取配置，需先通過身份驗證。

```
http://{host}:8888/{application}/{profile}
```

- `{host}`: 配置中心所在主機位址（例如：127.0.0.1）
- `{application}`: 應用名稱
- `{profile}`: 環境設定（dev、test、prod 等）

### 2. 範例請求

- 取得 eurekaservice 的 dev 環境配置
```
http://127.0.0.1:8888/eurekaservice/dev
```

- 此請求對應至的設定檔案位置 [eurekaservice-dev.yml](configs%2Feurekaservice-dev.yml)

### 3. 健康檢查與監控

使用 Spring Boot Actuator 提供的健康檢查端點：

```
http://localhost:8888/actuator/health
```

## 快速入門

### Config 客戶端初始化步驟

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

### 1. 自動部署 (GitHub Actions)

```bash
git checkout main
git pull --rebase origin main
git tag -a v0.0.1 -m "v0.0.1"
git push origin --tags
```

### 2. 手動部署

1. **建立資料夾**：
   ```shell
   sudo mkdir -p /opt/tata/configservice
   ```

2. **放置 JAR 文件**：將 configservice.jar 放入 /opt/tata/configservice

3. **建立 Dockerfile **：[參考範例](docs%2Fdocker%2Fconfigservice%2FDockerfile)
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
     -p 8888:8888 \
     -e SERVER_HOST=127.0.0.1 \
     -e SERVER_PORT=8888 \
     -e SECURITY_USERNAME=admin \
     -e SECURITY_PASSWORD=password \
     -e LINE_CHANNEL_ACCESS_TOKEN=LINE_CHANNEL_ACCESS_TOKEN \
     -e LINE_CHANNEL_SECRET=LINE_CHANNEL_SECRET \
     -e GROQ_API_KEY=GROQ_API_KEY \
     configservice
   ```

5. **確認狀態**：
   ```shell
   docker logs -f --tail 1000 configservice
   ```

### 3. 其他服務啟動命令

#### Eureka Service

```shell
# 啟動容器
docker run -d --name=eurekaservice -p 8761:8761 \
  -e SERVER_HOST=127.0.0.1 \
  -e SERVER_PORT=8761 \
  -e SECURITY_USERNAME=admin \
  -e SECURITY_PASSWORD=password \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e CONFIG_SERVER_USERNAME=admin \
  -e CONFIG_SERVER_PASSWORD=password \
  -e CONFIG_SERVER_URI=http://configservice:8888 \
  eurekaservice
```

#### API Gateway

```shell
# 啟動容器
docker run -d --name=gatewayservice -p 8080:8080 \
  -e SERVER_HOST=127.0.0.1 \
  -e SERVER_PORT=8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e CONFIG_SERVER_USERNAME=admin \
  -e CONFIG_SERVER_PASSWORD=password \
  -e CONFIG_SERVER_URI=http://127.0.0.1:8888 \
  -e EUREKA_SERVER_HOST=127.0.0.1 \
  -e EUREKA_SERVER_PORT=8761 \
  gatewayservice
```

## 版本管理

版本號採用語義化版本規範 (Semantic Versioning)：

- **MAJOR**: 不兼容的重大變更（例：1.0.0 → 2.0.0）
- **MINOR**: 新增功能，向下相容（例：1.1.0 → 1.2.0）
- **PATCH**: 修正錯誤，無功能變化（例：1.2.1 → 1.2.2）

## 常見問題

1. **連接配置中心失敗？**
   - 檢查網路連接
   - 驗證帳號密碼設定
   - 確認配置中心服務是否正常運行

## 參考資源

- [Spring Cloud Config 文檔](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)
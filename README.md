# Config Service

## 目錄

- [Config 服務](#config-服務)
    - [概述](#概述)
    - [參考資料](#參考資料)
    - [配置文件](#配置文件)
    - [訪問配置](#訪問配置)
    - [客戶端配置](#客戶端配置)
    - [部署到正式環境](#部署到正式環境)
    - [版本號更新指南](#版本號更新指南)
- [部署步驟](#部署步驟)
    - [建立資料夾](#建立資料夾)
    - [部署 JAR 文件](#部署-jar-文件)
    - [建立 Dockerfile](#建立-dockerfile)
    - [建構 Docker 映像檔](#建構-docker-映像檔)
    - [啟動服務](#啟動服務)
    - [確認服務啟動狀態](#確認服務啟動狀態)

---

## Config 服務

### 概述

- Config Service 採用 **Spring Cloud Config Server**，提供 **集中式管理與動態更新** 微服務配置，具備以下功能：
    - **配置存儲**：透過 Git 儲存，支持版本控制與歷史追蹤
    - **RESTful API**：提供 API 動態獲取配置，適用於 Kubernetes、Spring Boot 等應用

### 參考資料

- [Spring Cloud Config 文檔](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)
- [Spring Cloud 文檔](https://spring.io/projects/spring-cloud)

### 配置文件

- ConfigService 的主要配置文件：[application.yml](src%2Fmain%2Fresources%2Fapplication.yml)
    - 該文件配置了服務的端口、配置來源及註冊等資訊
- 其他服務配置文件 [configs](configs)

### 訪問配置

- 微服務可以通過 URL 訪問配置：
    - `http://localhost:8888/{application}/{profile}`
- 例如，要取得 `accountservice` 的 `dev` 環境下的配置，請訪問：
    - `http://localhost:8888/configservice/accountservice/dev`

### 客戶端配置

- 要使微服務能夠正確連接並從 ConfigService 獲取配置，需要在其 `bootstrap.yml` 文件中進行以下配置：

```yaml
spring:
  application:
    name: accountservice-dev
  cloud:
    config:
      uri: http://localhost:8888/configservice
```

### 部署到正式環境

```bash
# 1. 確保 main 分支是最新的
git checkout main
git fetch origin
git pull --rebase origin main

# 2. 為當前最新的 commit 打標籤
git tag -a v0.0.1 -m "版本 0.0.1"

# 3. 推送標籤到遠端倉庫，觸發 GitHub Actions
git push origin --tags
```

### 版本號更新指南

| 版本變更      | 說明          | 例子              |
|-----------|-------------|-----------------|
| **MAJOR** | 破壞性變更，不相容舊版 | `1.0.0 → 2.0.0` |
| **MINOR** | 新增功能，向下相容   | `1.1.0 → 1.2.0` |
| **PATCH** | 修 bug，不影響功能 | `1.2.1 → 1.2.2` |

---

## 部署步驟

### 建立資料夾

- 在伺服器上，建立存放服務的專用資料夾

```shell
sudo mkdir -p /opt/tata/configservice
```

### 部署 JAR 文件

將 configservice.jar 放入 /opt/tata/configservice

### 建立 Dockerfile

在 /opt/tata/configservice/ 目錄內，建立 Dockerfile

```shell
sudo touch /opt/tata/configservice/Dockerfile
sudo chown -R ubuntu:ubuntu /opt/tata/configservice/Dockerfile
```

完整 Dockerfile 可參考 [doc/Dockerfile](doc/Dockerfile)

### 建構 Docker 映像檔

```shell
cd /opt/tata/configservice
docker build --no-cache --progress=plain -t configservice .
```

### 啟動服務

```shell
docker run -di --name=configservice --network tata-network -p 8888:8888 configservice
```

### 確認服務啟動狀態

```shell
docker logs -f --tail 1000 configservice
```
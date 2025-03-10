# Config Service 文檔

## 目錄

- [Config 服務](#config-服務)
    - [概述](#概述)
    - [參考資料](#參考資料)
    - [配置文件](#配置文件)
    - [訪問配置](#訪問配置)
    - [客戶端配置](#客戶端配置)
- [Git Flow 開發流程](#git-flow-開發流程)
    - [分支策略：精簡工作流程 vs. 完整工作流程](#分支策略精簡工作流程-vs-完整工作流程)
    - [精簡工作流程：日常開發流程](#精簡工作流程日常開發流程)
    - [精簡工作流程：緊急修復流程](#精簡工作流程緊急修復流程)
    - [精簡工作流程：部署到正式環境](#精簡工作流程部署到正式環境)
    - [精簡工作流程：GitHub Actions](#精簡工作流程github-actions)
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

- Config Service 基於 Spring Cloud Config Server 提供集中式配置管理，支持微服務動態更新，具備以下功能：
    - **配置存儲**：存儲於 Git，支持版本控制與歷史追蹤
    - **RESTful API**：透過 API 提供配置，便於動態加載
    - **環境支持**：適用 dev、test、prod 等環境
    - **服務發現**：整合服務發現機制，實現微服務間發現

### 參考資料

- [Spring Cloud Config 文檔](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)
- [Spring Cloud 文檔](https://spring.io/projects/spring-cloud)

### 配置文件

- ConfigService 的主要配置文件：[application.yml](src%2Fmain%2Fresources%2Fapplication.yml)
    - 該文件配置了服務的端口、配置來源及註冊等資訊

### 訪問配置

- 微服務可以通過 URL 訪問配置：`http://localhost:8888/{application}/{profile}`
- 例如，要取得 `accountservice` 的 `dev` 環境下的配置，請訪問：
    - `http://localhost:8888/accountservice/dev`

### 客戶端配置

- 要使微服務能夠正確連接並從 ConfigService 獲取配置，需要在其 `bootstrap.yml` 文件中進行以下配置：

```yaml
spring:
  application:
    name: accountservice-dev
  cloud:
    config:
      uri: http://localhost:8888
```

---

## Git Flow 開發流程

- Git Flow 是一種結構化的 Git 工作流程，它提供完善的分支管理，確保開發、測試和正式環境的穩定性與可控性

### 分支策略：精簡工作流程 vs. 完整工作流程

| 團隊類型       | 分支名稱      | 角色   | 主要用途      | 合併來源                   | 合併目標      |
|------------|-----------|------|-----------|------------------------|-----------|
| **精簡工作流程** | `main`    | 正式環境 | 最終穩定版     | `develop`              | -         |
|            | `develop` | 開發環境 | 日常開發      | `feature/*`、`bugfix/*` | `main`    |
| **完整工作流程** | `main`    | 正式環境 | 最終穩定版     | `staging`              | -         |
|            | `staging` | 測試環境 | 測試開發完成的功能 | `develop`              | `main`    |
|            | `develop` | 開發環境 | 日常開發      | `feature/*`、`bugfix/*` | `staging` |

### 精簡工作流程：日常開發流程

```bash
# 1. 確保 develop 分支是最新的
git checkout develop
git fetch origin
git pull --rebase origin develop

# 2. 創建功能分支
git checkout -b YYYYMMDD-feature-新功能

# 3. 進行開發並提交
git add .
git commit -m "[feat] 實現某功能"

# 4. 在提交 PR 之前，確保分支是最新的
git fetch origin
git rebase origin/develop  # 避免不必要的合併提交

# 5. 推送功能分支
git push -u origin YYYYMMDD-feature-新功能

# 6. 在 GitHub 上建立 PR，請求合併到 develop 分支
#    (這步驟需要在 GitHub 上手動操作)

# 7. PR 通過後，同步本地 develop
git checkout develop
git fetch origin
git pull --rebase origin develop

# 8. 刪除已合併的功能分支
git branch -d YYYYMMDD-feature-新功能
git push origin --delete YYYYMMDD-feature-新功能

# 9. 確保 develop 最新後，開 PR 合併到 main
#    (這步驟需要在 GitHub 上手動操作)

# 10. PR 合併到 main 後，同步本地 main
git checkout main
git fetch origin
git pull --rebase origin main
```

### 精簡工作流程：緊急修復流程

```bash
# 1. 從 main 創建緊急修復分支
git checkout main
git fetch origin
git pull --rebase origin main
git checkout -b YYYYMMDD-hotfix-問題

# 2. 修復問題並提交
git add .
git commit -m "[fix] 修復問題"

# 3. 推送 hotfix 分支，並建立 PR 合併到 main
git push -u origin YYYYMMDD-hotfix-問題

# 4. **(GitHub 上)** 建立 PR，請求合併到 main
#    PR 審核通過後，main 會有最新修復

# 5. 本地同步 main
git checkout main
git fetch origin
git pull --rebase origin main

# 6. 同步修復到 develop
git checkout develop
git fetch origin
git pull --rebase origin develop
git rebase origin/main  # 避免 merge commit
git push

# 7. 刪除已合併的 hotfix 分支
git branch -d YYYYMMDD-hotfix-問題
git push origin --delete YYYYMMDD-hotfix-問題
```

### 精簡工作流程：部署到正式環境

```bash
# 1. 確保 main 分支是最新的
git checkout main
git fetch origin
git pull --rebase origin main

# 2. 為當前最新的 commit 打標籤
git tag -a v0.0.1 -m "版本 0.0.1"

# 3. 推送標籤到遠端倉庫
git push origin --tags
```

### 精簡工作流程：GitHub Actions

- [main.yml](.github%2Fworkflows%2Fmain.yml): 監聽新標籤事件，自動觸發持續部署流程

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
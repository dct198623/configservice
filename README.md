# Config Service

## **🔍 概述**

- Config Service 基於 Spring Cloud Config Server 提供集中式配置管理，支持微服務動態更新，具備以下功能：

    - 配置存儲：存儲於 Git，支持版本控制與歷史追蹤
    - RESTful API：透過 API 提供配置，便於動態加載
    - 環境支持：適用 dev、test、prod 等環境
    - 服務發現：整合 Eureka，實現微服務間發現

---

## **📚 參考資料**

- [Spring Cloud Config 文檔](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)
- [Spring Cloud Netflix Eureka 文檔](https://cloud.spring.io/spring-cloud-netflix/reference/html/)

---

## **🛠️ 配置文件**

- ConfigService 的主要配置文件：[application.yml](src%2Fmain%2Fresources%2Fapplication.yml)
    - 該文件配置了服務的端口、配置來源及 Eureka 註冊等資訊

---

## **🌍 訪問配置**

- 微服務可以通過 URL 訪問配置：http://localhost:8888/{application}/{profile}
- 例如，要取得 `accountservice` 的 `dev` 環境下的配置，請訪問：
    - http://localhost:8888/accountservice/dev

---

## **📡 客戶端配置**

- 要使微服務能夠正確連接並從 ConfigService 獲取配置，需要在其 `bootstrap.yml` 文件中進行以下配置：

```yaml
spring:
  application:
    name: accountservice-dev
  cloud:
    config:
      uri: http://localhost:8888
```
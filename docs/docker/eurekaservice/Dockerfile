# 使用 OpenJDK 21（Alpine 版）作為基礎映像，體積較小且適合容器化部署
FROM eclipse-temurin:21-jre-alpine

# 設定應用程式環境變數
## 伺服器設定
ENV SERVER_HOST=127.0.0.1
ENV SERVER_PORT=8761
## 安全設定
ENV SECURITY_USERNAME=admin
ENV SECURITY_PASSWORD=password
## 環境設定
ENV SPRING_PROFILES_ACTIVE=dev
## 配置伺服器設定
ENV CONFIG_SERVER_USERNAME=admin
ENV CONFIG_SERVER_PASSWORD=password
ENV CONFIG_SERVER_URI=http://configservice:8888

# 設定工作目錄為 /app
WORKDIR /app

# 複製編譯後的 JAR 檔案到容器內
COPY eurekaservice-*.jar /app/

# 確保 JAR 檔案已成功複製
RUN ls -la /app/ && ls -la /app/eurekaservice-*.jar || echo "No JAR found!"

# 建立指向最新 JAR 檔案的符號連結，確保一致的啟動方式
RUN set -e && \
    latest_jar=$(ls -t /app/eurekaservice-*.jar | head -n1) && \
    echo "Latest JAR: $latest_jar" && \
    ln -sf "$latest_jar" /app/eurekaservice.jar

# 以 Java 啟動應用程式
CMD ["java", "-jar", "/app/eurekaservice.jar"]
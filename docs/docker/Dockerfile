# 使用 OpenJDK 21（Alpine 版）作為基礎映像，體積較小且適合容器化部署
FROM eclipse-temurin:21-jre-alpine

# 設定工作目錄為 /app
WORKDIR /app

# 複製編譯後的 JAR 檔案到容器內
COPY build/libs/configservice-*.jar /app/configservice.jar

# 以 Java 啟動應用程式
CMD ["java", "-jar", "/app/configservice.jar"]
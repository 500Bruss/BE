FROM eclipse-temurin:17-jdk
WORKDIR /app

#COPY target/*.jar app.jar

COPY . .

# Build JAR với Maven Wrapper
RUN ./mvnw clean package -DskipTests

#EXPOSE 8080

#ENTRYPOINT ["java", "-jar", "app.jar"]

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "target/your-app.jar"]

EXPOSE 8080

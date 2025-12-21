# ==========================================
# STAGE 1: Builder (Extract Layers)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
# Copy the built JAR from your host machine into the builder stage
COPY build/libs/*.jar app.jar
# Extract the JAR into layers (dependencies, code, loader)
RUN java -Djarmode=layertools -jar app.jar extract

# ==========================================
# STAGE 2: Runtime (Production Image)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy layers from Builder stage (cached separately for speed)
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# Expose Ports
EXPOSE 8081
EXPOSE 8092
EXPOSE 3344/udp
EXPOSE 3355/udp

# Use the JarLauncher (optimized for layers) instead of 'java -jar'
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
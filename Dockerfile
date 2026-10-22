FROM eclipse-temurin:25-jre-alpine AS capas
WORKDIR /app
COPY target/dazma.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extraido

FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S dazma && adduser -S dazma -G dazma
WORKDIR /app
ARG OTEL_AGENT_VERSION=2.30.0
ADD --chmod=644 https://repo1.maven.org/maven2/io/opentelemetry/javaagent/opentelemetry-javaagent/${OTEL_AGENT_VERSION}/opentelemetry-javaagent-${OTEL_AGENT_VERSION}.jar /app/opentelemetry-javaagent.jar
COPY --from=capas /app/extraido/dependencies/ ./
COPY --from=capas /app/extraido/spring-boot-loader/ ./
COPY --from=capas /app/extraido/snapshot-dependencies/ ./
COPY --from=capas /app/extraido/application/ ./
ENV OTEL_SERVICE_NAME=dazma-api
RUN apk upgrade --no-cache libexpat
USER dazma
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-Duser.timezone=UTC", "-javaagent:/app/opentelemetry-javaagent.jar", "org.springframework.boot.loader.launch.JarLauncher"]

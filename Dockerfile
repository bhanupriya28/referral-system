FROM maven:3.8-openjdk-17-slim as builder
WORKDIR /src/ReferralSystem
COPY . /src/ReferralSystem
RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine as runtime
WORKDIR /app

# Install redis-cli
RUN apk add --no-cache redis

COPY --from=builder /src/ReferralSystem/target/*.jar /app/ReferralSystem.jar
COPY --from=builder /src/ReferralSystem/src/main/resources/application.properties /app/application.properties

CMD ["java", "-jar", "/app/ReferralSystem.jar","--server.address=0.0.0.0" , "--spring.config.location=/app/application.properties"]

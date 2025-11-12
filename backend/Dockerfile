FROM eclipse-temurin:17-jdk
LABEL maintainer="Gabriel Ferreira <ferreiragabriel2612@gmail.com>"
WORKDIR /app
EXPOSE 9191
COPY target/*.jar /app/users.jar
ENTRYPOINT ["java","-jar","users.jar"]
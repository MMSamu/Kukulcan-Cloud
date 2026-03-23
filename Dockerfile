# Imagen base con Java
FROM eclipse-temurin:17-jdk-alpine

# Carpeta de trabajo
WORKDIR /app

# Copiar el jar ya compilado
COPY target/*.jar app.jar

# Puerto
EXPOSE 8080

# Ejecutar aplicación
ENTRYPOINT ["java","-jar","/app/app.jar"]
# Imagen base con Java 17
FROM eclipse-temurin:17-jdk-alpine

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo .jar generado por Maven
COPY target/*.jar app.jar

# Puerto en el que corre tu app
EXPOSE 8081

# Comando para ejecutar la aplicación
ENTRYPOINT ["java","-jar","app.jar"]
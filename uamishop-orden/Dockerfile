# Imagen base con Java ligera
FROM eclipse-temurin:17-jdk-alpine

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiar el jar generado por Maven (asegúrate de correr 'mvn package' antes)
COPY target/*.jar app.jar

# Cambiamos a 8083 porque es el puerto de Órdenes
EXPOSE 8083

# Ejecutar la aplicación
ENTRYPOINT ["java","-jar","app.jar"]

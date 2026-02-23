/**
 * @file BackendApplication.java
 * @brief Clase principal de arranque de la aplicación Spring Boot.
 *
 * Esta clase es el punto de entrada (entry point) de toda la aplicación.
 *
 * Responsabilidades:
 * - Iniciar el contexto de Spring.
 * - Configurar automáticamente los componentes.
 * - Arrancar el servidor embebido (por defecto Tomcat).
 *
 * Anotación clave:
 * - @SpringBootApplication
 */

package com.uamishop.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @class BackendApplication
 *
 * Clase principal que contiene el método main.
 *
 * Al ejecutarse, inicia:
 * - El contenedor de Spring
 * - El escaneo de componentes
 * - La configuración automática
 * - El servidor web embebido
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * Método principal (entry point).
     *
     * Este método:
     * 1. Crea el contexto de aplicación.
     * 2. Realiza el component scanning.
     * 3. Aplica auto-configuración.
     * 4. Inicia el servidor web.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {

        SpringApplication.run(BackendApplication.class, args);
    }
}

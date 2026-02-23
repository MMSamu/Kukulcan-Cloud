package com.uamishop.backend.catalogo.exception;

/**
 * @file BusinessRuleException.java
 * @brief Excepción personalizada para reglas de negocio del módulo catálogo.
 *
 * Se lanza cuando una operación viola una regla de negocio
 * dentro del dominio de productos o categorías.
 *
 * @author Sebastian
 * @version 1.0
 * @since 1.0
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * @brief Código identificador de la regla de negocio violada.
     */
    private final String rule;

    /**
     * @brief Constructor que recibe únicamente el mensaje.
     *
     * @param message Mensaje descriptivo del error.
     */
    public BusinessRuleException(String message) {
        super(message);
        this.rule = null;
    }

    /**
     * @brief Constructor que recibe código de regla y mensaje.
     *
     * @param rule Código identificador de la regla.
     * @param message Mensaje descriptivo del error.
     */
    public BusinessRuleException(String rule, String message) {
        super(message);
        this.rule = rule;
    }

    /**
     * @brief Obtiene el código de la regla violada.
     *
     * @return Código de la regla o null si no se especificó.
     */
    public String getRule() {
        return rule;
    }
}
package com.uamishop.backend.shared.exception;

import lombok.Getter;
import java.io.Serial;

/**
 * * @brief Excepción lanzada cuando se viola una regla de negocio del dominio.
 * * @note Lau, en lugar de lanzar RuntimeException "pelonas" en nuestro Carrito.java,
 * podemos lanzar esta excepción. El GlobalExceptionHandler la va a atrapar solita.
 */

@Getter
public class BusinessRuleException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String rule;

    public BusinessRuleException(String message) {
        super(message);
        this.rule = null;
    }

    public BusinessRuleException(String rule, String message) {
        super(message);
        this.rule = rule;
    }

}
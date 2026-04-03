package com.uamishop.orden.controller.dto;

import jakarta.validation.constraints.*;

public record CancelacionRequest(
                @NotBlank(message = "El motivo de cancelación es obligatorio")

                @Size(min = 10, message = "El motivo debe tener al menos 10 caracteres")

                String motivo) {
}

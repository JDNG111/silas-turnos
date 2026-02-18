package com.silas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TurnRequestDTO {
    
    @NotNull(message = "El ID del servicio es requerido")
    private Long serviceId;
    
    @NotBlank(message = "El nombre del cliente es requerido")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String customerName;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener 10 dígitos")
    private String customerPhone;
    
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Formato de email inválido")
    private String customerEmail;
    
    private String priority; // Puede ser LOW, NORMAL, HIGH, VIP, EMERGENCY
    
    private String notes;
}
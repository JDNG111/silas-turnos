package com.silas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceDTO {
    
    private Long id;
    
    @NotBlank(message = "El código es requerido")
    private String code;
    
    @NotBlank(message = "El nombre es requerido")
    private String name;
    
    private String description;
    
    @NotNull(message = "El tiempo estimado es requerido")
    private Integer estimatedTimeMinutes;
    
    private Boolean active;
}
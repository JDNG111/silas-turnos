package com.silas.model;

public enum TurnStatus {
    PENDING("Pendiente"),
    CALLED("Llamado"),
    IN_PROGRESS("En Atención"),
    COMPLETED("Completado"),
    CANCELLED("Cancelado"),
    NO_SHOW("No se presentó");
    
    private final String description;
    
    TurnStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
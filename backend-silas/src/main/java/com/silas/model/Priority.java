package com.silas.model;

public enum Priority {
    LOW(1, "Baja"),
    NORMAL(2, "Normal"),
    HIGH(3, "Alta"),
    VIP(4, "VIP"),
    EMERGENCY(5, "Emergencia");
    
    private final int level;
    private final String description;
    
    Priority(int level, String description) {
        this.level = level;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDescription() {
        return description;
    }
}
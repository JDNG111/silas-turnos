package com.silas.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class TurnResponseDTO {
    private Long id;
    private String ticketNumber;
    private Long serviceId;
    private String serviceCode;
    private String serviceName;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String status;
    private String statusDescription;
    private String priority;
    private String priorityDescription;
    private Integer priorityLevel;
    private LocalDateTime createdAt;
    private String createdAtFormatted;
    private LocalDateTime calledAt;
    private String calledAtFormatted;
    private LocalDateTime startedAt;
    private String startedAtFormatted;
    private LocalDateTime completedAt;
    private String completedAtFormatted;
    private Integer waitingNumber;
    private String attentionPoint;
    private Integer estimatedWaitTime;
    private String notes;
    
    public String getCreatedAtFormatted() {
        if (createdAt != null) {
            return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return null;
    }
    
    public String getCalledAtFormatted() {
        if (calledAt != null) {
            return calledAt.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return null;
    }
}
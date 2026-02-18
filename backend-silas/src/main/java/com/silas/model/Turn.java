package com.silas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "turns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Turn {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String ticketNumber;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @Column(nullable = false)
    private String customerName;
    
    private String customerPhone;
    
    private String customerEmail;
    
    @Enumerated(EnumType.STRING)
    private TurnStatus status = TurnStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime calledAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private Integer waitingNumber;
    
    private String attentionPoint;
    
    private String notes;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
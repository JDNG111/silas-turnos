package com.silas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "turn_history")
@Data
public class TurnHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Turn turn;
    
    private String action; // CREATED, CALLED, STARTED, COMPLETED, CANCELLED
    
    private String performedBy;
    
    private LocalDateTime timestamp;
    
    private String details;
}
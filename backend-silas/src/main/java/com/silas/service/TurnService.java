package com.silas.service;

import com.silas.dto.TurnRequestDTO;
import com.silas.dto.TurnResponseDTO;
import com.silas.model.*;
import com.silas.repository.ServiceRepository;  // ¡OJO! Este es el repositorio de tu modelo Service
import com.silas.repository.TurnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;  // Esta es la anotación de Spring
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service  // Esta es la anotación de Spring
@RequiredArgsConstructor
public class TurnService {
    
    private final TurnRepository turnRepository;
    private final ServiceRepository serviceRepository;  // Este es tu repositorio
    
    @Transactional
    public TurnResponseDTO createTurn(TurnRequestDTO request) {
        // 1. Buscar el servicio (usando com.silas.model.Service)
        com.silas.model.Service service = serviceRepository.findById(request.getServiceId())
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + request.getServiceId()));
        
        // 2. Crear el turno
        Turn turn = new Turn();
        turn.setService(service);
        turn.setCustomerName(request.getCustomerName());
        turn.setCustomerPhone(request.getCustomerPhone());
        turn.setCustomerEmail(request.getCustomerEmail());
        turn.setNotes(request.getNotes());
        
        // 3. Asignar prioridad
        if (request.getPriority() != null && !request.getPriority().isEmpty()) {
            try {
                turn.setPriority(Priority.valueOf(request.getPriority().toUpperCase()));
            } catch (IllegalArgumentException e) {
                turn.setPriority(Priority.NORMAL);
            }
        } else {
            turn.setPriority(Priority.NORMAL);
        }
        
        // 4. Generar número de ticket
        turn.setTicketNumber(generateTicketNumber(service));
        
        // 5. Generar número de espera
        turn.setWaitingNumber(generateWaitingNumber(service));
        
        // 6. Guardar y retornar
        Turn savedTurn = turnRepository.save(turn);
        return convertToDTO(savedTurn);
    }
    
    private String generateTicketNumber(com.silas.model.Service service) {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String servicePart = service.getCode();
        
        Long todayCount = turnRepository.countByServiceAndStatus(service, TurnStatus.PENDING) + 
                          turnRepository.countByServiceAndStatus(service, TurnStatus.CALLED) +
                          turnRepository.countByServiceAndStatus(service, TurnStatus.IN_PROGRESS) +
                          turnRepository.countByServiceAndStatus(service, TurnStatus.COMPLETED) + 1;
        
        return String.format("%s-%s-%03d", datePart, servicePart, todayCount);
    }
    
    // En generateWaitingNumber, cambia a:
private Integer generateWaitingNumber(com.silas.model.Service service) {
    Integer maxNumber = turnRepository.findMaxWaitingNumberByService(service);
    return maxNumber + 1;
}
    
    @Transactional
    public TurnResponseDTO callTurn(Long turnId, String attentionPoint) {
        Turn turn = turnRepository.findById(turnId)
            .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnId));
        
        if (turn.getStatus() != TurnStatus.PENDING) {
            throw new RuntimeException("Solo se pueden llamar turnos pendientes. Estado actual: " + turn.getStatus());
        }
        
        turn.setStatus(TurnStatus.CALLED);
        turn.setCalledAt(LocalDateTime.now());
        turn.setAttentionPoint(attentionPoint);
        
        Turn updatedTurn = turnRepository.save(turn);
        return convertToDTO(updatedTurn);
    }
    
    @Transactional
    public TurnResponseDTO startTurn(Long turnId) {
        Turn turn = turnRepository.findById(turnId)
            .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnId));
        
        if (turn.getStatus() != TurnStatus.CALLED) {
            throw new RuntimeException("Solo se pueden iniciar turnos llamados. Estado actual: " + turn.getStatus());
        }
        
        turn.setStatus(TurnStatus.IN_PROGRESS);
        turn.setStartedAt(LocalDateTime.now());
        
        Turn updatedTurn = turnRepository.save(turn);
        return convertToDTO(updatedTurn);
    }
    
    @Transactional
    public TurnResponseDTO completeTurn(Long turnId) {
        Turn turn = turnRepository.findById(turnId)
            .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnId));
        
        if (turn.getStatus() != TurnStatus.IN_PROGRESS) {
            throw new RuntimeException("Solo se pueden completar turnos en progreso. Estado actual: " + turn.getStatus());
        }
        
        turn.setStatus(TurnStatus.COMPLETED);
        turn.setCompletedAt(LocalDateTime.now());
        
        Turn updatedTurn = turnRepository.save(turn);
        return convertToDTO(updatedTurn);
    }
    
    @Transactional
    public TurnResponseDTO cancelTurn(Long turnId, String reason) {
        Turn turn = turnRepository.findById(turnId)
            .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnId));
        
        if (turn.getStatus() == TurnStatus.COMPLETED || turn.getStatus() == TurnStatus.CANCELLED) {
            throw new RuntimeException("No se puede cancelar un turno " + turn.getStatus());
        }
        
        turn.setStatus(TurnStatus.CANCELLED);
        turn.setNotes((turn.getNotes() != null ? turn.getNotes() + " | " : "") + "Cancelado: " + reason);
        
        Turn updatedTurn = turnRepository.save(turn);
        return convertToDTO(updatedTurn);
    }
    
    @Transactional
    public TurnResponseDTO noShowTurn(Long turnId) {
        Turn turn = turnRepository.findById(turnId)
            .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnId));
        
        if (turn.getStatus() != TurnStatus.CALLED) {
            throw new RuntimeException("Solo se puede marcar como no presentado un turno llamado");
        }
        
        turn.setStatus(TurnStatus.NO_SHOW);
        
        Turn updatedTurn = turnRepository.save(turn);
        return convertToDTO(updatedTurn);
    }
    
    public List<TurnResponseDTO> getActiveTurns() {
        return turnRepository.findActiveTurnsOrderedByPriority().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<TurnResponseDTO> getTurnsByStatus(TurnStatus status) {
        return turnRepository.findByStatus(status).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public TurnResponseDTO getTurnByTicket(String ticketNumber) {
        Turn turn = turnRepository.findByTicketNumber(ticketNumber)
            .orElseThrow(() -> new RuntimeException("Turno no encontrado con ticket: " + ticketNumber));
        return convertToDTO(turn);
    }
    
    public List<TurnResponseDTO> getTurnsByService(Long serviceId) {
        com.silas.model.Service service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        return turnRepository.findByServiceAndStatus(service, TurnStatus.PENDING).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // Cambia de private a public
public TurnResponseDTO convertToDTO(Turn turn) {
    TurnResponseDTO dto = new TurnResponseDTO();
    dto.setId(turn.getId());
    dto.setTicketNumber(turn.getTicketNumber());
    dto.setServiceId(turn.getService().getId());
    dto.setServiceCode(turn.getService().getCode());
    dto.setServiceName(turn.getService().getName());
    dto.setCustomerName(turn.getCustomerName());
    dto.setCustomerPhone(turn.getCustomerPhone());
    dto.setCustomerEmail(turn.getCustomerEmail());
    dto.setStatus(turn.getStatus().toString());
    dto.setStatusDescription(turn.getStatus().getDescription());
    dto.setPriority(turn.getPriority().toString());
    dto.setPriorityDescription(turn.getPriority().getDescription());
    dto.setPriorityLevel(turn.getPriority().getLevel());
    dto.setCreatedAt(turn.getCreatedAt());
    dto.setCalledAt(turn.getCalledAt());
    dto.setStartedAt(turn.getStartedAt());
    dto.setCompletedAt(turn.getCompletedAt());
    dto.setWaitingNumber(turn.getWaitingNumber());
    dto.setAttentionPoint(turn.getAttentionPoint());
    dto.setNotes(turn.getNotes());
    
    // Calcular tiempo estimado de espera
    if (turn.getStatus() == TurnStatus.PENDING) {
        int turnsAhead = turnRepository.findByServiceAndStatus(turn.getService(), TurnStatus.PENDING).size();
        dto.setEstimatedWaitTime(turnsAhead * turn.getService().getEstimatedTimeMinutes());
    }
    
    return dto;
}
}
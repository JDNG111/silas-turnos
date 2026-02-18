package com.silas.controller;

import com.silas.dto.TurnRequestDTO;
import com.silas.dto.TurnResponseDTO;
import com.silas.model.TurnStatus;
import com.silas.service.TurnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turns")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class TurnController {
    
    private final TurnService turnService;
    
    @PostMapping
    public ResponseEntity<TurnResponseDTO> createTurn(@Valid @RequestBody TurnRequestDTO request) {
        TurnResponseDTO response = turnService.createTurn(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<TurnResponseDTO>> getActiveTurns() {
        return ResponseEntity.ok(turnService.getActiveTurns());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TurnResponseDTO>> getTurnsByStatus(@PathVariable String status) {
        try {
            TurnStatus turnStatus = TurnStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(turnService.getTurnsByStatus(turnStatus));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado no válido: " + status);
        }
    }
    
    @GetMapping("/ticket/{ticketNumber}")
    public ResponseEntity<TurnResponseDTO> getTurnByTicket(@PathVariable String ticketNumber) {
        return ResponseEntity.ok(turnService.getTurnByTicket(ticketNumber));
    }
    
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<TurnResponseDTO>> getTurnsByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(turnService.getTurnsByService(serviceId));
    }
    
    @PutMapping("/{id}/call")
    public ResponseEntity<TurnResponseDTO> callTurn(
            @PathVariable Long id,
            @RequestParam String attentionPoint) {
        return ResponseEntity.ok(turnService.callTurn(id, attentionPoint));
    }
    
    @PutMapping("/{id}/start")
    public ResponseEntity<TurnResponseDTO> startTurn(@PathVariable Long id) {
        return ResponseEntity.ok(turnService.startTurn(id));
    }
    
    @PutMapping("/{id}/complete")
    public ResponseEntity<TurnResponseDTO> completeTurn(@PathVariable Long id) {
        return ResponseEntity.ok(turnService.completeTurn(id));
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<TurnResponseDTO> cancelTurn(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "Cancelado por operador") : "Cancelado por operador";
        return ResponseEntity.ok(turnService.cancelTurn(id, reason));
    }
    
    @PutMapping("/{id}/noshow")
    public ResponseEntity<TurnResponseDTO> noShowTurn(@PathVariable Long id) {
        return ResponseEntity.ok(turnService.noShowTurn(id));
    }
}
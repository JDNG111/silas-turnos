package com.silas.service;

import com.silas.dto.DashboardDTO;
import com.silas.dto.TurnResponseDTO;
import com.silas.model.Turn;
import com.silas.model.TurnStatus;
import com.silas.repository.TurnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final TurnRepository turnRepository;
    private final TurnService turnService;
    
    public DashboardDTO getDashboardData() {
        DashboardDTO dashboard = new DashboardDTO();
        
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);
        
        // Obtener todos los turnos del día
        List<Turn> todayTurns = turnRepository.findAll().stream()
            .filter(t -> t.getCreatedAt().isAfter(startOfDay) && t.getCreatedAt().isBefore(endOfDay))
            .collect(Collectors.toList());
        
        // Métricas básicas
        dashboard.setTotalTurnsToday((long) todayTurns.size());
        dashboard.setPendingTurns(todayTurns.stream()
            .filter(t -> t.getStatus() == TurnStatus.PENDING).count());
        dashboard.setInProgressTurns(todayTurns.stream()
            .filter(t -> t.getStatus() == TurnStatus.IN_PROGRESS).count());
        dashboard.setCompletedTurns(todayTurns.stream()
            .filter(t -> t.getStatus() == TurnStatus.COMPLETED).count());
        
        // Tiempo promedio de espera (en minutos)
        double avgWait = todayTurns.stream()
            .filter(t -> t.getCalledAt() != null)
            .mapToLong(t -> java.time.Duration.between(t.getCreatedAt(), t.getCalledAt()).toMinutes())
            .average()
            .orElse(0.0);
        dashboard.setAverageWaitTime(avgWait);
        
        // Turnos por servicio
        List<Map<String, Object>> turnsByService = new ArrayList<>();
        todayTurns.stream()
            .collect(Collectors.groupingBy(t -> t.getService().getName()))
            .forEach((service, turns) -> {
                Map<String, Object> item = new HashMap<>();
                item.put("service", service);
                item.put("count", turns.size());
                turnsByService.add(item);
            });
        dashboard.setTurnsByService(turnsByService);
        
        // Turnos por hora
        List<Map<String, Object>> turnsByHour = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            final int h = hour;
            long count = todayTurns.stream()
                .filter(t -> t.getCreatedAt().getHour() == h)
                .count();
            Map<String, Object> item = new HashMap<>();
            item.put("hour", String.format("%02d:00", h));
            item.put("count", count);
            turnsByHour.add(item);
        }
        dashboard.setTurnsByHour(turnsByHour);
        
        // Turnos recientes
        List<TurnResponseDTO> recentTurns = turnRepository.findAll().stream()
            .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
            .limit(10)
            .map(turnService::convertToDTO)
            .collect(Collectors.toList());
        dashboard.setRecentTurns(recentTurns);
        
        return dashboard;
    }
}
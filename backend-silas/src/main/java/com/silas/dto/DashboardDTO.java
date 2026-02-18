package com.silas.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDTO {
    private Long totalTurnsToday;
    private Long pendingTurns;
    private Long inProgressTurns;
    private Long completedTurns;
    private Double averageWaitTime;
    private List<Map<String, Object>> turnsByService;
    private List<Map<String, Object>> turnsByHour;
    private List<TurnResponseDTO> recentTurns;
}
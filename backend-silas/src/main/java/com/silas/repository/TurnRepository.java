package com.silas.repository;

import com.silas.model.Turn;
import com.silas.model.TurnStatus;
import com.silas.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnRepository extends JpaRepository<Turn, Long> {
    
    // Búsquedas básicas que siempre funcionan
    Optional<Turn> findByTicketNumber(String ticketNumber);
    
    List<Turn> findByStatus(TurnStatus status);
    
    List<Turn> findByServiceAndStatus(Service service, TurnStatus status);
    
    // Query con JPQL estándar (funciona en todos los dialectos)
    @Query("SELECT t FROM Turn t WHERE t.status IN ('PENDING', 'CALLED', 'IN_PROGRESS') ORDER BY t.priority DESC, t.createdAt ASC")
    List<Turn> findActiveTurnsOrderedByPriority();
    
    // Versión simplificada sin funciones de fecha
    @Query("SELECT COALESCE(MAX(t.waitingNumber), 0) FROM Turn t WHERE t.service = :service")
    Integer findMaxWaitingNumberByService(@Param("service") Service service);
    
    Long countByServiceAndStatus(Service service, TurnStatus status);
    
    List<Turn> findByServiceAndStatusOrderByPriorityDescCreatedAtAsc(Service service, TurnStatus status);
}
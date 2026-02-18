package com.silas.controller;

import com.silas.dto.ServiceDTO;
import com.silas.model.Service;
import com.silas.repository.ServiceRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ServiceController {
    
    private final ServiceRepository serviceRepository;
    
    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        List<ServiceDTO> services = serviceRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<ServiceDTO>> getActiveServices() {
        List<ServiceDTO> services = serviceRepository.findByActiveTrue().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Long id) {
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        return ResponseEntity.ok(convertToDTO(service));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<ServiceDTO> getServiceByCode(@PathVariable String code) {
        Service service = serviceRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        return ResponseEntity.ok(convertToDTO(service));
    }
    
    @PostMapping
    public ResponseEntity<ServiceDTO> createService(@Valid @RequestBody ServiceDTO serviceDTO) {
        if (serviceRepository.existsByCode(serviceDTO.getCode())) {
            throw new RuntimeException("Ya existe un servicio con el código: " + serviceDTO.getCode());
        }
        
        Service service = convertToEntity(serviceDTO);
        Service savedService = serviceRepository.save(service);
        return new ResponseEntity<>(convertToDTO(savedService), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceDTO serviceDTO) {
        
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        // Actualizar campos
        service.setName(serviceDTO.getName());
        service.setDescription(serviceDTO.getDescription());
        service.setEstimatedTimeMinutes(serviceDTO.getEstimatedTimeMinutes());
        if (serviceDTO.getActive() != null) {
            service.setActive(serviceDTO.getActive());
        }
        
        Service updatedService = serviceRepository.save(service);
        return ResponseEntity.ok(convertToDTO(updatedService));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        
        // Soft delete (desactivar en lugar de eliminar)
        service.setActive(false);
        serviceRepository.save(service);
        
        return ResponseEntity.noContent().build();
    }
    
    private ServiceDTO convertToDTO(Service service) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(service.getId());
        dto.setCode(service.getCode());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setEstimatedTimeMinutes(service.getEstimatedTimeMinutes());
        dto.setActive(service.getActive());
        return dto;
    }
    
    private Service convertToEntity(ServiceDTO dto) {
        Service service = new Service();
        service.setCode(dto.getCode());
        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setEstimatedTimeMinutes(dto.getEstimatedTimeMinutes());
        service.setActive(dto.getActive() != null ? dto.getActive() : true);
        return service;
    }
}
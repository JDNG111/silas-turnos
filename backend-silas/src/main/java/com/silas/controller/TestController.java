package com.silas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping
    public Map<String, String> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "SILAS API funcionando correctamente");
        response.put("status", "ok");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return response;
    }
    
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
package com.silas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SilasApplication {
    public static void main(String[] args) {
        SpringApplication.run(SilasApplication.class, args);
        System.out.println("🚀 SILAS Backend iniciado correctamente");
    }
}
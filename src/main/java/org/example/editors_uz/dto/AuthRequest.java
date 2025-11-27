package org.example.editors_uz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Username bo'sh bo'lmasligi kerak")
    @Size(min = 3, max = 50, message = "Username 3-50 belgi oralig'ida bo'lishi kerak")
    private String username;

    @NotBlank(message = "Parol bo'sh bo'lmasligi kerak")
    @Size(min = 6, message = "Parol kamida 6 belgidan iborat bo'lishi kerak")
    private String password;
}







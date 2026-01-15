package me.kiporenko.system.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

// ✅ RECOMMENDED - Custom DTO (self-written)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {  // <-- YOU would create this
    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
    private String errorCode;
}

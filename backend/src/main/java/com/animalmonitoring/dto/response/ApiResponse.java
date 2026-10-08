package com.animalmonitoring.dto.response;

import java.time.LocalDateTime;

/**
 * Generic wrapper for consistent API responses.
 */
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public boolean isSuccess()           { return success; }
    public void setSuccess(boolean s)    { this.success = s; }
    public String getMessage()           { return message; }
    public void setMessage(String m)     { this.message = m; }
    public T getData()                   { return data; }
    public void setData(T data)          { this.data = data; }
    public LocalDateTime getTimestamp()  { return timestamp; }
    public void setTimestamp(LocalDateTime t) { this.timestamp = t; }
}

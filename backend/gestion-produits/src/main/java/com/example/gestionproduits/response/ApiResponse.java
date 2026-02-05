package com.example.gestionproduits.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse {
    private boolean succes;
    private String message;
    private Object data;
    private LocalDateTime timestamp;

    public ApiResponse(String message, Object data){
        this.succes = true;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(boolean succes, String message, Object data){
        this.succes = succes;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static ApiResponse success(String message, Object data){
        return new ApiResponse(true, message, data);
    }

    public static  ApiResponse error(String message){
        return new ApiResponse(false, message, null);
    }

    public boolean isSuccess(boolean success){
        return success;
    }

    public void setSuccess(boolean success){
        this.succes = success;
    }

}

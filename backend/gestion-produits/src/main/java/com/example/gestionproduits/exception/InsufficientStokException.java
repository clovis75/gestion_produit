package com.example.gestionproduits.exception;

public class InsufficientStokException extends RuntimeException{
    public InsufficientStokException(String message){
        super(message);
    }
}

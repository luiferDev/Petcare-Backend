package com.Petcare.Petcare.Exception.Business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SitterNotFoundException extends RuntimeException {
    public SitterNotFoundException(Long id) {
        super("Cuidador no encontrado con ID: " + id);
    }

    public SitterNotFoundException(String message) {
        super(message);
    }
}

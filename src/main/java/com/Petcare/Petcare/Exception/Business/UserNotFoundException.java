package com.Petcare.Petcare.Exception.Business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Usuario no encontrado con el ID " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
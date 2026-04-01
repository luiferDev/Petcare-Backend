package com.Petcare.Petcare.Exception.Business;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ServiceOfferingInactiveException extends RuntimeException {
    public ServiceOfferingInactiveException(Long id) {
        super("Service offering is inactive with id: " + id);
    }
}

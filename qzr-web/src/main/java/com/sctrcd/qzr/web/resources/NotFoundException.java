package com.sctrcd.qzr.web.resources;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when a resource cannot be found. For example, if a
 * request is made for an entity which is not in the database.
 * 
 * @author Stephen Masters
 */
@ResponseStatus(value = org.springframework.http.HttpStatus.NOT_FOUND, reason = "Resource not found.")
public class NotFoundException extends QuizException {

    private static final long serialVersionUID = 1L;
    
    public NotFoundException() {
        this("NOT_FOUND", "Resource not found.");
    }
    
    public NotFoundException(String message) {
        this("NOT_FOUND", message);
    }
    
    public NotFoundException(String code, String message) {
        super(code, message);
    }
    
    public NotFoundException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
    
}

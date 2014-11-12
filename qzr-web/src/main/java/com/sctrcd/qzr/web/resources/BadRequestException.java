package com.sctrcd.qzr.web.resources;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when a bad request is made. i.e. If an argument is not valid.
 * 
 * @author Stephen Masters
 */
@ResponseStatus(value = org.springframework.http.HttpStatus.BAD_REQUEST, reason = "Bad request.")
public class BadRequestException extends QuizException {

    private static final long serialVersionUID = 1L;
    
    public BadRequestException() {
        this("BAD_REQUEST", "Bad request.");
    }
    
    public BadRequestException(String message) {
        this("BAD_REQUEST", message);
    }
    
    public BadRequestException(String code, String message) {
        super(code, message);
    }
    
    public BadRequestException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
    
}

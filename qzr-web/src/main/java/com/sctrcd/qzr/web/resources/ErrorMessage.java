package com.sctrcd.qzr.web.resources;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An error message, which will be returned to the client formatted as JSON/XML.
 * 
 * @author Stephen Masters
 */
@XmlRootElement(name = "error")
public class ErrorMessage {

    private Throwable exception;
    private String message;

    public ErrorMessage() {
        this.message = "";
    }

    public ErrorMessage(String message) {
        this.message = message;
    }

    public ErrorMessage(Throwable exception) {
        this.exception = exception;
        this.message = exception.getLocalizedMessage();
    }

    @XmlTransient
    @JsonIgnore
    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @XmlElement(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

package com.sctrcd.qzr.web.resources;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
public class ErrorMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String message;
    
    public ErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public ErrorMessage(QuizException ex) {
        this.code = ex.getCode();
        this.message = ex.getMessage();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return "Error [" + code + "]:" + getMessage();
    }

}

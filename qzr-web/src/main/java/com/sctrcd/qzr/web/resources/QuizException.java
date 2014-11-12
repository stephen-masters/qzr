package com.sctrcd.qzr.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude()
public class QuizException extends Exception {

    private static final long serialVersionUID = 1L;

    private String code;

    public QuizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public QuizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String toString() {
        return "Error [" + code + "]:" + getMessage();
    }

}

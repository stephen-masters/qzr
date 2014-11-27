package com.sctrcd.qzr.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sctrcd.qzr.web.controllers.HrMaxQuizController;

/**
 * Used by exceptions within the application to provide a code and a more
 * descriptive reason. Exception handlers in the {@link HrMaxQuizController} are
 * designed to parse this so that a nicely structured JSON message is returned.
 * 
 * @author Stephen Masters
 */
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

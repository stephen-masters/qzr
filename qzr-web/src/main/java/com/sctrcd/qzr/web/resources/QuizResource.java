package com.sctrcd.qzr.web.resources;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Provides details about a quiz. Not much detail here, as the main purpose is
 * to add links pointing at the various REST endpoints.
 * 
 * @author Stephen Masters
 */
@JsonInclude(Include.NON_EMPTY)
@XmlRootElement(name = "question")
public class QuizResource extends ResourceSupport {

    private String title;

    public QuizResource() {
    }

    public QuizResource(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String toString() {
        return "Quiz: " + title;
    }

}

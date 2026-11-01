package com.sctrcd.qzr.web.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.hateoas.RepresentationModel;

/**
 * Provides details about a quiz. Not much detail here, as the main purpose is
 * to add links pointing at the various REST endpoints.
 *
 * @author Stephen Masters
 */
@JsonInclude(Include.NON_EMPTY)
public class QuizResource extends RepresentationModel<QuizResource> {

    private String title;

    public QuizResource() {}

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

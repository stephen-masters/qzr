package com.sctrcd.qzr.web.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.web.controllers.HealthQuizController;

@Component
public class QuestionResourceAssembler extends ResourceAssemblerSupport<Question, QuestionResource> {

    public QuestionResourceAssembler() {
        super(HealthQuizController.class, QuestionResource.class);
    }

    @Override
    public QuestionResource toResource(Question question) {

        QuestionResource resource = createResourceWithId("questions/" + question.getKey(), question);

        resource.setKey(question.getKey());
        resource.setText(question.getQuestion().toString());
        
        resource.addOptions(question.getOptions());

        resource.add(linkTo(methodOn(HealthQuizController.class).answer(new AnswerResource())).withRel("answer"));

        return resource;
    }

}

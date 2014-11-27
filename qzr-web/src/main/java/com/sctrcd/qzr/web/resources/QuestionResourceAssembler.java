package com.sctrcd.qzr.web.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.web.controllers.HrMaxQuizController;

/**
 * Injected into the {@link HrMaxQuizController} as a tool for building {@link QuestionResource}.
 * 
 * @author Stephen Masters
 */
@Component
public class QuestionResourceAssembler extends ResourceAssemblerSupport<Question, QuestionResource> {

    @Autowired
    AnswerResourceAssembler answerResourceAssembler;
    
    public QuestionResourceAssembler() {
        super(HrMaxQuizController.class, QuestionResource.class);
    }

    @Override
    public QuestionResource toResource(Question question) {

        System.out.println("Question: " + question);
        
        QuestionResource resource = createResourceWithId("questions/" + question.getKey(), question);

        resource.setKey(question.getKey());
        resource.setText(question.getQuestion().toString());
        resource.setAnswerType(question.getAnswerType());
        
        resource.addOptions(question.getOptions());

        if (question.getAnswer() != null) {
            resource.setAnswer(answerResourceAssembler.toResource(question.getAnswer()));
        }
        
        try {
            resource.add(linkTo(methodOn(HrMaxQuizController.class)
                    .answer(question.getKey(), new AnswerResource()))
                    .withRel("answer"));
        } catch (BadRequestException e) {
            // Do nothing...
        }

        return resource;
    }

}

package com.sctrcd.qzr.web.resources;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.sctrcd.qzr.facts.Answer;
import com.sctrcd.qzr.web.controllers.HrMaxQuizController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

/**
 * Injected into the {@link HrMaxQuizController} as a tool for building {@link AnswerResource}.
 *
 * @author Stephen Masters
 */
@Component
public class AnswerResourceAssembler extends RepresentationModelAssemblerSupport<Answer, AnswerResource> {

    public AnswerResourceAssembler() {
        super(HrMaxQuizController.class, AnswerResource.class);
    }

    @Override
    public AnswerResource toModel(Answer answer) {

        AnswerResource resource = createModelWithId("questions/" + answer.getKey() + "/answer", answer);

        resource.setKey(answer.getKey());
        if (answer.getValue() != null) {
            resource.setValue(answer.getValue().toString());
        }
        resource.setWhen(answer.getWhen());

        if (answer.getQuestion() != null) {
            try {
                resource.add(linkTo(methodOn(HrMaxQuizController.class).getQuestion(answer.getKey()))
                        .withRel("question"));
            } catch (NotFoundException e) {
                // Do nothing ... the exception cannot be thrown.
            }
        }

        return resource;
    }
}

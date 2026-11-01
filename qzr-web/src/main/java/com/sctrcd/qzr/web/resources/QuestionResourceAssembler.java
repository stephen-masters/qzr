package com.sctrcd.qzr.web.resources;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.web.controllers.HrMaxQuizController;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

/**
 * Injected into the {@link HrMaxQuizController} as a tool for building {@link QuestionResource}.
 *
 * @author Stephen Masters
 */
@Component
public class QuestionResourceAssembler extends RepresentationModelAssemblerSupport<Question, QuestionResource> {

    private final AnswerResourceAssembler answerResourceAssembler;

    public QuestionResourceAssembler(AnswerResourceAssembler answerResourceAssembler) {
        super(HrMaxQuizController.class, QuestionResource.class);
        this.answerResourceAssembler = answerResourceAssembler;
    }

    @Override
    public QuestionResource toModel(Question question) {

        QuestionResource resource = createModelWithId("questions/" + question.getKey(), question);

        resource.setKey(question.getKey());
        resource.setText(question.getQuestion());
        resource.setAnswerType(question.getAnswerType());

        resource.addOptions(question.getOptions());

        if (question.getAnswer() != null) {
            resource.setAnswer(answerResourceAssembler.toModel(question.getAnswer()));
        }

        try {
            resource.add(linkTo(methodOn(HrMaxQuizController.class).answer(question.getKey(), new AnswerResource()))
                    .withRel("answer"));
        } catch (BadRequestException e) {
            // Do nothing...
        }

        return resource;
    }
}

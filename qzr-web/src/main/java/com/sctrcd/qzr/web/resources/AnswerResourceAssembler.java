package com.sctrcd.qzr.web.resources;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import com.sctrcd.qzr.facts.Answer;
import com.sctrcd.qzr.web.controllers.HealthQuizController;

@Component
public class AnswerResourceAssembler extends ResourceAssemblerSupport<Answer, AnswerResource> {

    public AnswerResourceAssembler() {
        super(HealthQuizController.class, AnswerResource.class);
    }

    @Override
    public AnswerResource toResource(Answer known) {

        AnswerResource resource = createResourceWithId("answers/" + known.getKey(), known);

        resource.setKey(known.getKey());
        resource.setValue(known.getValue().toString());
        resource.setWhen(known.getWhen());

//        resource.add(linkTo(
//                methodOn(HealthQuizController.class).getAnswer(known.getKey()))
//                .withSelfRel());

        return resource;
    }

}

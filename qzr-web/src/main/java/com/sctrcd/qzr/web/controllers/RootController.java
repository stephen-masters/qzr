package com.sctrcd.qzr.web.controllers;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sctrcd.qzr.facts.Answer;
import com.sctrcd.qzr.facts.HrMax;
import com.sctrcd.qzr.facts.Known;
import com.sctrcd.qzr.facts.KnownList;
import com.sctrcd.qzr.facts.Question;
import com.sctrcd.qzr.services.HrMaxQuizServiceImpl;
import com.sctrcd.qzr.services.QuizService;
import com.sctrcd.qzr.web.resources.AnswerResource;
import com.sctrcd.qzr.web.resources.AnswerResourceAssembler;
import com.sctrcd.qzr.web.resources.BadRequestException;
import com.sctrcd.qzr.web.resources.ErrorMessage;
import com.sctrcd.qzr.web.resources.NotFoundException;
import com.sctrcd.qzr.web.resources.QuestionResource;
import com.sctrcd.qzr.web.resources.QuestionResourceAssembler;
import com.sctrcd.qzr.web.resources.QuizResource;

@Controller
public class RootController {

    private static Logger log = LoggerFactory.getLogger(RootController.class);

    @Autowired
    private QuizService svc;
    @Autowired
    private AnswerResourceAssembler answerAssembler;
    @Autowired
    private QuestionResourceAssembler questionAssembler;

    public RootController() {
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public String getQuiz() throws NotFoundException {
        model.addAttribute("messages", messageRepository.findAll());
        return "message/list";
    }

}

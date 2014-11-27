package com.sctrcd.qzr.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sctrcd.qzr.services.HrMaxQuizService;
import com.sctrcd.qzr.web.resources.AnswerResourceAssembler;
import com.sctrcd.qzr.web.resources.QuestionResourceAssembler;

@Controller
public class HomeController {

    @Autowired
    private HrMaxQuizService svc;
    @Autowired
    private AnswerResourceAssembler answerAssembler;
    @Autowired
    private QuestionResourceAssembler questionAssembler;

    public HomeController() {
    }

    @RequestMapping("/")
    String index() {
        return "index";
    }

}

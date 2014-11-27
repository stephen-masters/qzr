package com.sctrcd.qzr.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Provides the request mapping for the home page. As this is a single-page app,
 * this is the only mapping needed.
 * 
 * @author Stephen Masters
 */
@Controller
public class HomeController {

    public HomeController() {
    }

    /**
     * This will render using the Thymeleaf template:
     * <code>src/main/resources/templates/index.html</code>
     */
    @RequestMapping("/")
    String index() {
        return "index";
    }

}

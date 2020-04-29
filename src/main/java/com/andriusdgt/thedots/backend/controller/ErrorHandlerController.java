package com.andriusdgt.thedots.backend.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
final class ErrorHandlerController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @RequestMapping(ERROR_PATH)
    Map<String, String> error() {
        return new HashMap<>(Map.of("Message", "Task failed successfully"));
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

}

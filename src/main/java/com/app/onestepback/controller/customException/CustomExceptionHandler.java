package com.app.onestepback.controller.customException;

import com.app.onestepback.exception.CustomException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected RedirectView handleCustomException(CustomException customException){
        return new RedirectView("/member/login?login=false");
    }
}
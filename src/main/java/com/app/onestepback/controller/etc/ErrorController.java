package com.app.onestepback.controller.etc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/error/*")
public class ErrorController {

    @GetMapping("error")
    public void goToErrorForm(){;}
}

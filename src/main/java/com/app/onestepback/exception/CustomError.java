package com.app.onestepback.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomError implements ErrorController {
    @GetMapping("/error")
    public String handleError(){
        return "error/error";
    }

    @GetMapping("no-artist")
    public String noArtist(){
        return "error/no-artist";
    }
}

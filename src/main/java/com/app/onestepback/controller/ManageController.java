package com.app.onestepback.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/manage/*")
public class ManageController {

    @GetMapping("announcement-write")
    public void goToAnnouncementWriteForm(){;}

    @GetMapping("funding")
    public void goToFundingForm(){;}

    @GetMapping("inquiry")
    public void goToInquiryForm(){;}

    @GetMapping("member")
    public void goToMemberForm(){;}

    @GetMapping("post")
    public void goToPostForm(){;}
}

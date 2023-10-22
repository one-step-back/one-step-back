package com.app.onestepback.controller;

import com.app.onestepback.service.GetCountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetCountsController {
    private final GetCountsService getCountsService;

    @GetMapping("/getCounts")
    public int[] getCounts(@RequestParam("id") Long id) {
        int[] counts = new int[3];

        counts[0] = getCountsService.getCountOfSubscriber(id);
        counts[1] = getCountsService.getCountOfPost(id);
        counts[2] = getCountsService.getCountOfVideo(id);

        return counts;
    }
}

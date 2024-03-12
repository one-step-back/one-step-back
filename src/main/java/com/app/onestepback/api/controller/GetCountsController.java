package com.app.onestepback.api.controller;

import com.app.onestepback.service.etc.GetCountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetCountsController {
    private final GetCountsService getCountsService;

    // todo: 사용하지 않도록 대체제를 만들 것(N+1 문제를 강제함)
    @GetMapping("/getCounts")
    public int[] getCounts(@RequestParam("id") Long id) {
        int[] counts = new int[3];

        counts[0] = getCountsService.getCountOfSubscriber(id);
        counts[1] = getCountsService.getCountOfPost(id);
        counts[2] = getCountsService.getCountOfVideo(id);

        return counts;
    }
}

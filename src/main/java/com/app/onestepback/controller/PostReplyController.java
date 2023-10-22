package com.app.onestepback.controller;

import com.app.onestepback.domain.Pagination;
import com.app.onestepback.domain.PostReplyDTO;
import com.app.onestepback.domain.PostReplyVO;
import com.app.onestepback.service.PostReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/replies/*")
public class PostReplyController {
    private final PostReplyService postReplyService;

    @GetMapping("getAllReplies")
    public Map<String, Object> loadReplies(Pagination pagination, @RequestParam("postId") Long postId, @RequestParam("page") int page) {
        // 리플라이는 rest로 작동해야 하므로 결과적으로 두개의 값을 보내야 됨
        // 1. 리플라이 내용들
        // 2. 리스트로 보냄과 동시에 이게 리플라이들의 마지막인지 아닌지를 구분할 수 있는 flag

        // 전체 리플라이의 숫자설정과 현재 보여줘야 할 페이지, 한번에 보여질 양의 갯수 설정 후 이후의 모든걸 계산하는 프로그레스
        pagination.setTotal(postReplyService.getReplyCount(postId));
        pagination.setPage(page);
        pagination.setRowCount(20);
        pagination.progress();



        // 리스트로 반납되는 현재의 리플라이들이 마지막인지 아닌지를 표시해주는 플래그
        boolean isItEnd = false;
        // 만약 현재의 페이지가 마지막 페이지라면 true값 반납 & 애초부터 댓글이 없다면 강제적 트루값 반환
        if (page == pagination.getRealEnd()){
            isItEnd = true;
        } else if (pagination.getTotal() == 0) {
            isItEnd = true;
        }

        List<PostReplyDTO> replies = postReplyService.getAllReplies(postId, pagination);

        // 해쉬맵을 이용하여 두개의 독립적인 값들을 반납
        Map<String, Object> response = new HashMap<>();
        response.put("replies", replies);
        response.put("isItEnd", isItEnd);

        return response;
    }

    @PostMapping("write")
    public void write(@RequestBody PostReplyVO postReplyVO){
        log.info(postReplyVO.toString());
        postReplyService.saveReply(postReplyVO);
    }
}

package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ArtistVO {
//    회원 번호
    private Long memberId;
//    아티스트 블로그 이름
    private String artistBlogName;
//    아티스트 소개
    private String artistDescription;
//    블로그 사진 이름
    private String blogImgName;
//    블로그 사진 경로
    private String blogImgPath;
//    아티스트 생성일자(승격)
    private String artistCreateTime;
//    아티스트 정보 수정일자
    private String artistUpdateTime;
}

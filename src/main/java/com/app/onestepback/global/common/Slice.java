package com.app.onestepback.global.common;

import lombok.Getter;

import java.util.List;

/**
 * 무한 스크롤 및 페이징 처리를 위한 슬라이스(Slice) 전송 객체입니다.
 * <p>
 * 클라이언트가 요청한 크기(size)보다 1개 더 많은 데이터를 데이터베이스에서 조회한 뒤,
 * 초과분 1개의 존재 여부로 다음 페이지의 유무(hasNext)를 판단하고
 * 실제 반환할 데이터(content)를 정확한 크기로 구성합니다.
 * </p>
 *
 * @param <T> 데이터 목록의 타입
 */
@Getter
public class Slice<T> {

    /**
     * 클라이언트에게 반환되는 실제 데이터 목록
     */
    private final List<T> content;

    /**
     * 다음 페이지 데이터의 존재 여부
     */
    private final boolean hasNext;

    /**
     * 클라이언트가 최초에 요청한 페이지 크기
     */
    private final int size;

    /**
     * 조회된 데이터 목록과 요청 크기를 기반으로 슬라이스 객체를 초기화합니다.
     *
     * @param content 데이터베이스에서 조회된 원본 데이터 목록 (요청 크기 + 1개)
     * @param size    클라이언트가 요청한 페이지 크기 기준값
     */
    public Slice(List<T> content, int size) {
        this.size = size;

        if (content.size() > size) {
            this.hasNext = true;
            content.remove(size);
        } else {
            this.hasNext = false;
        }

        this.content = content;
    }
}
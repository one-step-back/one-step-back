package com.app.onestepback.mapper;

import com.app.onestepback.domain.vo.BookmarkedVideoVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface BookmarkedVideoMapper {

    void insert(Long postId, Long memberId);

    void delete(Long postId, Long memberId);

}

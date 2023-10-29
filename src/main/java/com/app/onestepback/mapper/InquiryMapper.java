package com.app.onestepback.mapper;

import com.app.onestepback.domain.InquiryVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InquiryMapper {
    public void insert(InquiryVO inquiryVO);
}

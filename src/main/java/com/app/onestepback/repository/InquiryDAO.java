package com.app.onestepback.repository;

import com.app.onestepback.domain.vo.InquiryVO;
import com.app.onestepback.mapper.InquiryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InquiryDAO {
    private final InquiryMapper inquiryMapper;
    public void saveInquiry(InquiryVO inquiryVO){
        inquiryMapper.insert(inquiryVO);
    }
}

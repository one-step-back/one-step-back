package com.app.onestepback.service.etc;

import com.app.onestepback.domain.vo.InquiryVO;
import com.app.onestepback.repository.InquiryDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {
    private final InquiryDAO inquiryDAO;
    @Override
    public void saveInquiry(InquiryVO inquiryVO) {
        inquiryDAO.saveInquiry(inquiryVO);
    }
}

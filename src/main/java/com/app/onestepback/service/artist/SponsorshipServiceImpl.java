package com.app.onestepback.service.artist;

import com.app.onestepback.domain.vo.SponsorshipVO;
import com.app.onestepback.repository.SponsorshipDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SponsorshipServiceImpl implements SponsorshipService {
    private final SponsorshipDAO sponsorshipDAO;
    @Override
    public void sponsorshipDone(SponsorshipVO sponsorshipVO) {
        sponsorshipDAO.sponsorshipDone(sponsorshipVO);
    }
}

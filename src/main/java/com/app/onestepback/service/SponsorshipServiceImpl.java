package com.app.onestepback.service;

import com.app.onestepback.domain.SponsorshipVO;
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

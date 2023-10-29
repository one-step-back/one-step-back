package com.app.onestepback.repository;

import com.app.onestepback.domain.SponsorshipVO;
import com.app.onestepback.mapper.SponsorshipMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SponsorshipDAO {
    private final SponsorshipMapper sponsorshipMapper;

    public void sponsorshipDone(SponsorshipVO sponsorshipVO){
        sponsorshipMapper.insert(sponsorshipVO);
    }
}

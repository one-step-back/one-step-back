package com.app.onestepback.mapper;

import com.app.onestepback.domain.SponsorshipVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SponsorshipMapper {
    public void insert(SponsorshipVO sponsorshipVO);
}

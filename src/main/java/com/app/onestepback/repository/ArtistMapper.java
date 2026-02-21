package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.artist.ArtistBankAccountDTO;
import com.app.onestepback.domain.dto.artist.ArtistDTO;
import com.app.onestepback.domain.dto.artist.ArtistHomeDTO;
import com.app.onestepback.domain.dto.artist.ArtistPageDTO;
import com.app.onestepback.domain.model.ArtistVO;
import com.app.onestepback.domain.type.artist.ArtistSortType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ArtistMapper {
    // ===================================================================================
    // CREATE
    // ===================================================================================
    void insert(ArtistVO artistVO);


    // ===================================================================================
    // READ
    // ===================================================================================
    Optional<Long> selectId(Long memberId);

    Optional<ArtistPageDTO> selectArtistPageInfo(Long artistId, Long viewerId);

    ArtistDTO.MyArtists selectMyArtists(Long memberId);

    List<ArtistDTO.ListInfo> selectArtistList(@Param("memberId") Long memberId, @Param("sort") String sort);

    Optional<ArtistHomeDTO.FundingHighlight> selectHomeFunding(Long artistId);

    List<ArtistHomeDTO.RecentFeed> selectHomeFeeds(Long artistId);

    List<ArtistHomeDTO.MembershipInfo> selectHomeMemberships(Long artistId);

    boolean checkBankInfoExistence(Long artistId);

    Optional<ArtistBankAccountDTO> selectBankAccount(Long artistId);

    Optional<ArtistDTO.BankInfo> selectBankInfo(Long memberId);


    // ===================================================================================
    // UPDATE
    // ===================================================================================
    void updateArtist(ArtistVO artistVO);

    void updateAccount(@Param("artistId") Long artistId, @Param("dto") ArtistBankAccountDTO dto);
}
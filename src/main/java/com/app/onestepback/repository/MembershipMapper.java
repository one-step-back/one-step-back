package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.membership.MembershipDetailDTO;
import com.app.onestepback.domain.dto.membership.MembershipDTO;
import com.app.onestepback.domain.model.MembershipVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MembershipMapper {
    void insert(MembershipVO membershipVO);

    boolean existsById(Long id);

    Optional<MembershipVO> selectById(Long id);

    List<MembershipVO> selectAllByArtistId(Long artistId);

    List<MembershipDTO> selectAllDtoByArtistId(Long artistId, Long viewerId);

    Optional<MembershipDetailDTO> selectDetailDtoById(@Param("id") Long membershipId);

    boolean existsByArtistAndLevel(@Param("artistId") Long artistId, @Param("level") Integer level);

    int update(MembershipVO membershipVO);

    int updateStatus(@Param("id") Long id, @Param("artistId") Long artistId, @Param("status") String status);

    int delete(@Param("id") Long id, @Param("artistId") Long artistId);
}
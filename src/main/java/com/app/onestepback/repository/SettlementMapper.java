package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.settlement.SettlementDTO;
import com.app.onestepback.domain.model.SettlementVO;
import com.app.onestepback.domain.type.settlement.SettlementStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SettlementMapper {
    // ==========================================
    //  Create
    // ==========================================
    void insertSettlement(SettlementVO settlementVO);

    // ==========================================
    //  Read
    // ==========================================
    Long selectUnsettledTotalAmount(Long artistId);

    Long selectCompletedTotalAmount(Long artistId);

    List<SettlementDTO.MonthlyRevenue> selectMonthlyRevenue(Long artistId);

    List<SettlementDTO.History> selectSettlementHistory(
            @Param("artistId") Long artistId,
            @Param("lastId") Long lastId,
            @Param("limit") int limit
    );

    List<SettlementVO> selectRequestedSettlements();

    // ==========================================
    //  Update
    // ==========================================
    void updateSettlementStatus(@Param("id") Long id, @Param("status") SettlementStatus status);
}
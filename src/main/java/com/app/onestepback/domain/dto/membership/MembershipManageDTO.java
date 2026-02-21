package com.app.onestepback.domain.dto.membership;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MembershipManageDTO {
    private boolean hasAppliedBankAccount;
    private List<MembershipDTO> memberships;
}

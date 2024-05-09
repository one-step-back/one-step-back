package com.app.onestepback.domain.entity.cf;

import com.app.onestepback.domain.base.Period;
import com.app.onestepback.domain.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_FUNDING_PAYMENT")
@SequenceGenerator(name = "SEQ_FUNDING_PAYMENT_GENERATOR", sequenceName = "SEQ_FUNDING_PAYMENT", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundingPayment extends Period {
    @Id @GeneratedValue(generator = "SEQ_FUNDING_PAYMENT_GENERATOR")
    private Long id;

    @Column(name = "FUNDING_AMOUNT_MONEY", nullable = false)
    private long amountMoney;

    @ManyToOne
    @JoinColumn(name = "FUNDING_ID")
    private CrowdFunding funding;
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}

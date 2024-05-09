package com.app.onestepback.domain.entity.etc;

import com.app.onestepback.domain.base.Period;
import com.app.onestepback.domain.entity.member.Artist;
import com.app.onestepback.domain.entity.member.Member;
import com.app.onestepback.domain.type.etc.PaymentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_SPONSORSHIP")
@SequenceGenerator(name = "SEQ_SPONSORSHIP_GENERATOR", sequenceName = "SEQ_SPONSORSHIP", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sponsorship extends Period {
    @Id @GeneratedValue(generator = "SEQ_SPONSORSHIP_GENERATOR")
    private Long id;

    @Column(name = "SPONSOR_NAME", nullable = false)
    private String name;
    @Column(name = "SPONSOR_EMAIL", nullable = false)
    private String email;
    @Column(name = "SPONSOR_TEL", nullable = false)
    private String tel;
    @Column(name = "SPONSORSHIP_MONEY", nullable = false)
    private Long money;
    @Column(name = "PAYMENT_STATUS", nullable = false)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "ARTIST_ID")
    private Artist artist;
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}

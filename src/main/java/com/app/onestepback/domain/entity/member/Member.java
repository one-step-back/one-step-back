package com.app.onestepback.domain.entity.member;

import com.app.onestepback.domain.base.Period;
import com.app.onestepback.domain.type.member.MemberStatus;
import lombok.*;

import javax.persistence.*;

@Entity @Table(name = "TBL_MEMBER")
@SequenceGenerator(name = "SEQ_MEMBER_GENERATOR", sequenceName = "SEQ_MEMBER", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends Period {
    @Id @GeneratedValue(generator = "SEQ_MEMBER_GENERATOR")
    private Long id;
    @Column(nullable = false, name = "MEMBER_EMAIL", unique = true)
    private String email;
    @Column(nullable = false, name = "MEMBER_PASSWORD")
    private String password;
    @Column(nullable = false, name = "MEMBER_NICKNAME")
    private String nickname;
    @Column(name = "MEMBER_KAKAO_PROFILE_URL")
    private String kakaoProfileUrl;
    @Column(name = "MEMBER_PROFILE_URL")
    private String profileName;
    @Column(name = "MEMBER_PROFILE_PATH")
    private String profilePath;
    @Column(name = "MEMBER_INTRODUCTION")
    private String introduction;
    @Column(name = "MEMBER_PAYMENT_TOTAL")
    private Long totalPayment;
    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBER_STATUS")
    private MemberStatus status = MemberStatus.ACTIVE;

    @OneToOne(mappedBy = "member")
    @PrimaryKeyJoinColumn
    private Artist artist;
}

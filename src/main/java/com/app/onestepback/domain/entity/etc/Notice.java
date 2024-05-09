package com.app.onestepback.domain.entity.etc;

import com.app.onestepback.domain.base.Period;
import com.app.onestepback.domain.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_NOTICE")
@SequenceGenerator(name = "SEQ_NOTICE_GENERATOR", sequenceName = "SEQ_NOTICE", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends Period {
    @Id @GeneratedValue(generator = "SEQ_NOTICE_GENERATOR")
    private Long id;

    @Column(name = "NOTICE_TITLE", nullable = false)
    private String title;
    @Lob @Column(name = "NOTICE_CONTENT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}

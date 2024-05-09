package com.app.onestepback.domain.entity.post;

import com.app.onestepback.domain.base.Period;
import com.app.onestepback.domain.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_POST_REPLY")
@SequenceGenerator(name = "SEQ_POST_REPLY_GENERATOR", sequenceName = "SEQ_POST_REPLY", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostReply extends Period {
    @Id @GeneratedValue(generator = "SEQ_POST_REPLY_GENERATOR")
    private Long id;

    @Column(nullable = false, length = 1000, name = "REPLY_CONTENT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "POST_ID")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}

package com.app.onestepback.domain.entity.post;

import com.app.onestepback.domain.base.Period;
import com.app.onestepback.domain.entity.member.Member;
import com.app.onestepback.domain.type.post.PostStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_POST")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = "SEQ_POST_GENERATOR", sequenceName = "SEQ_POST", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Post extends Period {
    @Id @GeneratedValue(generator = "SEQ_POST_GENERATOR")
    private Long id;

    @Column(nullable = false, name = "POST_TITLE")
    private String title;
    @Column(name = "POST_SUBTITLE")
    private String subtitle;
    @Lob @Column(nullable = false, name = "POST_CONTENT")
    private String content;
    @Column(nullable = false, name = "POST_CATEGORY", length = 100)
    private String category;
    @Column(name = "POST_VIEW_COUNT")
    private Long viewCount = 0L;
    @Enumerated(EnumType.STRING)
    @Column(name = "POST_STATUS")
    private PostStatus status = PostStatus.READABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}

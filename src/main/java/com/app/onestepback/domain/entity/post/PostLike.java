package com.app.onestepback.domain.entity.post;

import com.app.onestepback.domain.compositePk.PostLikeId;
import com.app.onestepback.domain.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_POST_LIKE")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {
    @EmbeddedId
    private PostLikeId id;

    @ManyToOne
    @JoinColumn(name = "POST_ID", insertable = false, updatable = false)
    private Post post;
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", insertable = false, updatable = false)
    private Member member;
}

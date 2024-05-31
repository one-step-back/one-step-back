package com.app.onestepback.domain.entity.post;

import com.app.onestepback.domain.compositePk.BookmarkedPostId;
import com.app.onestepback.domain.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_BOOKMARKED_VIDEO")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkedVideo {
    @EmbeddedId
    private BookmarkedPostId id;

    @ManyToOne
    @JoinColumn(name = "POST_ID", insertable = false, updatable = false)
    private Video post;
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", insertable = false, updatable = false)
    private Member member;
}

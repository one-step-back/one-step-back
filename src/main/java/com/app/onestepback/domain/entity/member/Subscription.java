package com.app.onestepback.domain.entity.member;

import com.app.onestepback.domain.compositePk.SubscriptionId;
import lombok.*;

import javax.persistence.*;

@Entity @Table(name = "TBL_SUBSCRIPTION")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription {

    @EmbeddedId
    private SubscriptionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTIST_ID", insertable = false, updatable = false)
    private Artist artist;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", insertable = false, updatable = false)
    private Member member;

    @Builder
    public Subscription(SubscriptionId id, Artist artist, Member member) {
        this.id = id;
        this.artist = artist;
        this.member = member;
    }
}

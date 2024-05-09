package com.app.onestepback.domain.compositePk;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class SubscriptionId implements Serializable {
    @Column(name = "ARTIST_ID")
    private Long artistId;
    @Column(name = "MEMBER_ID")
    private Long memberId;

    public SubscriptionId(Long artistId, Long memberId) {
        this.artistId = artistId;
        this.memberId = memberId;
    }
}

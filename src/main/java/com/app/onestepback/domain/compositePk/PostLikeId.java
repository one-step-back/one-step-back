package com.app.onestepback.domain.compositePk;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class PostLikeId implements Serializable {
    @Column(name = "POST_ID")
    private Long postId;
    @Column(name = "MEMBER_ID")
    private Long memberId;
}

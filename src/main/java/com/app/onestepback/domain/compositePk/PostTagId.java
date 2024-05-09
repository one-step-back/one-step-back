package com.app.onestepback.domain.compositePk;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class PostTagId implements Serializable {
    @Column(name = "POST_ID")
    private Long postId;
    @Column(name = "POST_TAG_NAME")
    private String tagName;
}

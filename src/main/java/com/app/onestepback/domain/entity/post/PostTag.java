package com.app.onestepback.domain.entity.post;

import com.app.onestepback.domain.compositePk.PostTagId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "TBL_POST_TAG")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {
    @EmbeddedId
    private PostTagId id;

    @ManyToOne
    @JoinColumn(name = "POST_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Post post;

    @Column(name = "POST_TAG_NAME", nullable = false, length = 50, insertable = false, updatable = false)
    private String tagName;
}


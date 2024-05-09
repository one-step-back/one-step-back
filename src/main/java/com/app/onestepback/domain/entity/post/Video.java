package com.app.onestepback.domain.entity.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity @Table(name = "TBL_VIDEO")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video extends Post{
    @Column(name = "VIDEO_LINK", nullable = false)
    private String link;
}

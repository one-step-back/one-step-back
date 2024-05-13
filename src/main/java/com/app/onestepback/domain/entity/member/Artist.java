package com.app.onestepback.domain.entity.member;

import com.app.onestepback.domain.base.Period;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_ARTIST")
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Artist extends Period {
    @Id
    @Column(name = "ARTIST_ID")
    private Long artistId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "ARTIST_ID", referencedColumnName = "ID")
    private Member member;
    @Column(nullable = false, name = "ARTIST_BLOG_NAME", length = 100)
    private String blogName;
    @Column(name = "ARTIST_DESCRIPTION", length = 600)
    private String description;
    @Column(name = "BLOG_IMG_NAME")
    private String blogImgName;
    @Column(name = "BLOG_IMG_PATH")
    private String blogImgPath;
}

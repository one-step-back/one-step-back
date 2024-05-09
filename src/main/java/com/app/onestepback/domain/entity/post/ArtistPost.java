package com.app.onestepback.domain.entity.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_ARTIST_POST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistPost extends Post {
}

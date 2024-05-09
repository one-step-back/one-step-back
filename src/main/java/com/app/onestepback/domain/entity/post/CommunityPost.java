package com.app.onestepback.domain.entity.post;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity @Table(name = "TBL_COMMUNITY_POST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityPost extends Post {
}

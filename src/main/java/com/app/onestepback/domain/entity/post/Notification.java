package com.app.onestepback.domain.entity.post;

import com.app.onestepback.domain.entity.member.Member;
import com.app.onestepback.domain.type.post.NotificationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_NOTIFICATION")
@SequenceGenerator(name = "SEQ_NOTIFICATION_GENERATOR", sequenceName = "SEQ_NOTIFICATION", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id @GeneratedValue(generator = "SEQ_NOTIFICATION_GENERATOR")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NOTIFICATION_STATUS")
    private NotificationStatus status = NotificationStatus.UNREAD;

    @ManyToOne
    @JoinColumn(name = "POST_ID")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}

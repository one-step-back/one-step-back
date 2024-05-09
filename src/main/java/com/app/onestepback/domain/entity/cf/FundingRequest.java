package com.app.onestepback.domain.entity.cf;

import com.app.onestepback.domain.entity.member.Artist;
import com.app.onestepback.domain.entity.member.Member;
import com.app.onestepback.domain.type.cf.RequestStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_FUNDING_REQUEST")
@SequenceGenerator(name = "SEQ_FUNDING_REQUEST_GENERATOR", sequenceName = "SEQ_FUNDING_REQUEST", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundingRequest {
    @Id @GeneratedValue(generator = "SEQ_FUNDING_REQUEST")
    private Long id;

    @Column(name = "REQUEST_FUNDING_TITLE", nullable = false, length = 100)
    private String title;
    @Column(name = "REQUEST_FUNDING_CONTENT", nullable = false, length = 1000)
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(name = "REQUEST_FUNDING_STATUS")
    private RequestStatus status = RequestStatus.REQUESTED;

    @ManyToOne
    @JoinColumn(name = "ARTIST_ID")
    private Artist artist;
    @ManyToOne
    @JoinColumn(name = "WRITER_ID")
    private Member writer;
}

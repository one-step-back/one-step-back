package com.app.onestepback.domain.entity.cf;

import com.app.onestepback.domain.base.Period;
import com.app.onestepback.domain.entity.member.Artist;
import com.app.onestepback.domain.type.cf.FundingStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_CROWDFUNDING")
@SequenceGenerator(name = "SEQ_CROWDFUNDING_GENERATOR", sequenceName = "SEQ_CROWDFUNDING", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrowdFunding extends Period {
    @Id @GeneratedValue(generator = "SEQ_CROWDFUNDING_GENERATOR")
    private Long id;

    @Column(name = "FUNDING_TITLE", nullable = false, length = 100)
    private String title;
    @Column(name = "FUNDING_CONTENT", nullable = false, length = 1000)
    private String content;
    @Column(name = "FUNDING_ING_NAME")
    private String imgName;
    @Column(name = "FUNDING_IMG_PATH")
    private String imgPath;
    @Column(name = "FUNDING_SUM_COLLECTED", nullable = false)
    private long sumCollected = 0;
    @Column(name = "FUNDING_TARGET_AMOUNT", nullable = false)
    private long targetAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "FUNDING_STATUS")
    private FundingStatus status = FundingStatus.ONGOING;

    @ManyToOne
    @JoinColumn(name = "ARTIST_ID", nullable = false, updatable = false)
    private Artist artist;
}

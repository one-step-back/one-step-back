package com.app.onestepback.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SponsorshipVO {
    private Long id;
    private Long artistId;
    private Long memberId;
    private String sponsorName;
    private String sponsorEmail;
    private String sponsorTel;
    private Long sponsorshipMoney;
    private String paymentStatus;
    private String paymentTime;
    private String paymentCanceledTime;
}

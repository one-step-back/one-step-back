package com.app.onestepback.domain.dto.notification;

import com.app.onestepback.domain.model.NotificationVO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO {
    private Long id;
    private String notiMessage;
    private String notiUrl;

    public static NotificationDTO from(NotificationVO vo) {
        return NotificationDTO.builder()
                .id(vo.getId())
                .notiMessage(vo.getNotiMessage())
                .notiUrl(vo.getNotiUrl())
                .build();
    }
}
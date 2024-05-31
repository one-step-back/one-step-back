package com.app.onestepback.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class TimeUtil {
    public String getTimeGap(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();
        long gap = ChronoUnit.SECONDS.between(time, now);

        if (gap < 60) {
            return "방금 전";
        }
        if (gap < 3600) {
            return String.format("%d분 전", gap / 60);
        }
        if (gap < 86400) {
            return String.format("%d시간 전", gap / 3600);
        }
        if (gap < 2678400) {
            return String.format("%d일 전", gap / 86400);
        }
        if (gap < 32140800) {
            return String.format("%d개월 전", gap / 2678400);
        }
        return String.format("%d년 전", gap / 32140800);
    }
}

package com.app.onestepback.domain.base;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter @ToString
public abstract class Period {
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
}

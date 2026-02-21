package com.app.onestepback.repository;

import com.app.onestepback.domain.model.NotificationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void insert(NotificationVO notificationVO);

    List<NotificationVO> selectUnreadNotifications(Long receiverId);

    void updateIsRead(@Param("id") long notificationId);
}
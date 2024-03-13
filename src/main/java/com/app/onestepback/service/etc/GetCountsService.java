package com.app.onestepback.service.etc;

public interface GetCountsService {
    public int getCountOfSubscriber(Long artistId);
    public int getCountOfPost(Long memberId);
    public int getCountOfVideo(Long memberId);
}

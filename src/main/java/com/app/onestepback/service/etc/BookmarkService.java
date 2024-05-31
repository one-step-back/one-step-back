package com.app.onestepback.service.etc;

public interface BookmarkService {

    boolean doBookmarkArtistPost(Long postId, Long memberId, boolean status);

    boolean doBookmarkVideo(Long postId, Long memberId, boolean status);

}

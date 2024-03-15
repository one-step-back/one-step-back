package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.ArtistDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostDTO;
import com.app.onestepback.domain.dto.VideoPostDTO;
import com.app.onestepback.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistServiceImpl implements ArtistService {
    private final ArtistDAO artistDAO;
    private final ArtistPostDAO artistPostDAO;
    private final VideoPostDAO videoPostDAO;
    private final PostTagDAO postTagDAO;

//    아티스트 정보 가져오기
    @Override
    public Optional<ArtistDTO> getArtist(Long memberId) {
        return artistDAO.getArtist(memberId);
    }


    @Override
    public List<ArtistPostDTO> get3Posts(Long memberId) {
        List<ArtistPostDTO> posts = artistPostDAO.get3Posts(memberId);

        for (ArtistPostDTO post : posts){
            List<String> tags = postTagDAO.getAllTags(post.getId());

            if (!tags.isEmpty()) {
                post.setTag1(tags.get(0));
            }
            if (tags.size() >= 2) {
                post.setTag2(tags.get(1));
            }
            if (tags.size() >= 3) {
                post.setTag3(tags.get(2));
            }
            if (tags.size() >= 4) {
                post.setTag4(tags.get(3));
            }
            if (tags.size() >= 5) {
                post.setTag5(tags.get(4));
            }
        }
        return posts;
    }

    @Override
    public List<VideoPostDTO> get3Videos(Long memberId) {
        List<VideoPostDTO> videos = videoPostDAO.get3Videos(memberId);

        for (VideoPostDTO video : videos){
            List<String> tags = postTagDAO.getAllTags(video.getId());

            if (!tags.isEmpty()) {
                video.setTag1(tags.get(0));
            }
            if (tags.size() >= 2) {
                video.setTag2(tags.get(1));
            }
            if (tags.size() >= 3) {
                video.setTag3(tags.get(2));
            }
            if (tags.size() >= 4) {
                video.setTag4(tags.get(3));
            }
            if (tags.size() >= 5) {
                video.setTag5(tags.get(4));
            }
        }
        return videos;
    }
}

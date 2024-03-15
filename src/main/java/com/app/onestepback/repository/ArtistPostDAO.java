package com.app.onestepback.repository;

import com.app.onestepback.domain.dto.artist.ArtistPostDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostListDTO;
import com.app.onestepback.domain.dto.artist.ArtistPostRegisterDTO;
import com.app.onestepback.domain.vo.Pagination;
import com.app.onestepback.mapper.ArtistPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArtistPostDAO {
    private final ArtistPostMapper artistPostMapper;

    public int getCountOfPost(Long memberId){return artistPostMapper.selectCountOfPost(memberId);}

    public List<ArtistPostDTO> get3Posts(Long memberId){return artistPostMapper.select3Posts(memberId);}

    public List<ArtistPostListDTO> getArtistPostsPage(Long memberId, Pagination pagination){return artistPostMapper.selectAll(memberId, pagination);}

    public void savePost(ArtistPostRegisterDTO artistPostRegisterDTO){artistPostMapper.insertPost(artistPostRegisterDTO);}

    public void saveArtistPost(Long postId){artistPostMapper.insertArtistPost(postId);}

    public ArtistPostDTO getPost(Long id){return artistPostMapper.select(id);}

    public Optional<ArtistPostDTO> getPrevPost(ArtistPostDTO artistPostDTO){return artistPostMapper.selectPrevPost(artistPostDTO);}

    public Optional<ArtistPostDTO> getNextPost(ArtistPostDTO artistPostDTO){return artistPostMapper.selectNextPost(artistPostDTO);}

    public void editPost(ArtistPostDTO artistPostDTO){
        artistPostMapper.update(artistPostDTO);
    }
}

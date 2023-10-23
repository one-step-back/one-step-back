package com.app.onestepback.service;

import com.app.onestepback.repository.PostFileDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostFileServiceImpl implements PostFileService {
    private final PostFileDAO postFileDAO;
    @Override
    public void eraseFile(Long id) {
        postFileDAO.eraseFile(id);
    }
}

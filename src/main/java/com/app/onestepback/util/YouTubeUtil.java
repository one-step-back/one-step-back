package com.app.onestepback.util;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class YouTubeUtil {

    public String getYouTubeThumbnailUrl(String youtubeLink) {
        // YouTube 비디오 ID 추출을 위한 정규 표현식
        String pattern = "v=([^&]+)";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeLink);

        if (matcher.find()) {
            return "https://img.youtube.com/vi/" + matcher.group(1) + "/mqdefault.jpg";
        }
        return null;
    }
}

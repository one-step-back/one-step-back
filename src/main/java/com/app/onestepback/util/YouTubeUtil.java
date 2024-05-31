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

    public String getYouTubeEmbedLink(String link) {
        // YouTube 링크에서 비디오 ID를 추출하기 위한 정규 표현식
        String regex = "(?:\\?v=|&v=|youtu\\.be\\/|embed\\/|\\/v\\/|\\/e\\/|watch\\?v=)([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(link);

        if (matcher.find()) {
            String videoId = matcher.group(1);
            return "https://www.youtube.com/embed/" + videoId + "?si=UW0il-4Lpr4jUYbK";
        }
        return null;
    }
}

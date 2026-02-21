package com.app.onestepback.global.dummy;

import com.app.onestepback.service.member.MemberService;
import com.app.onestepback.service.member.cmd.MemberRegisterCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberDummyGenerator {

    private final MemberService memberService;
    private final ResourcePatternResolver resourcePatternResolver;

    public void generate() {
        if (memberService.existsByEmail("user@test.com")) {
            log.info("Member dummy data already exists. Skipping execution.");
            return;
        }

        // 1. 이미지 로드
        List<Resource> profileImages = loadProfileImages();

        // 2. 닉네임 리스트 준비 (Shuffle)
        List<String> nicknameSquad = getRealKoreanNicknames();
        Collections.shuffle(nicknameSquad);

        // 3. 메인 테스트 계정 (The Alpha)
        createMember("user@test.com", "관리자", getProfileImage(profileImages, 0));

        // 4. 더미 계정 양산 (50명)
        for (int i = 1; i <= 50; i++) {
            Resource imageResource = null;

            // 4명 중 1명(25%)은 프로필 사진 없음
            if (i % 4 != 0) {
                imageResource = getProfileImage(profileImages, i);
            }

            // 닉네임 할당
            String realNickname;
            if (i - 1 < nicknameSquad.size()) {
                realNickname = nicknameSquad.get(i - 1);
            } else {
                realNickname = "유저_" + i;
            }

            createMember("user" + i + "@test.com", realNickname, imageResource);
        }

        log.info("MemberDummyGenerator: 51 units deployed successfully.");
    }

    private List<Resource> loadProfileImages() {
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:static/images/dummy/profile/profile*");
            return Arrays.stream(resources)
                    .filter(Resource::exists)
                    .filter(resource -> {
                        String filename = resource.getFilename();
                        return filename != null && isImageFile(filename);
                    })
                    .sorted(Comparator.comparing(Resource::getFilename))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("MemberDummyGenerator: Failed to load images. Proceeding without them.");
            return Collections.emptyList();
        }
    }

    private void createMember(String email, String nickname, Resource imageResource) {
        MultipartFile profileMultipart = null;
        if (imageResource != null && imageResource.exists()) {
            try {
                profileMultipart = new DummyMultipartFile(imageResource);
            } catch (IOException e) {
                log.error("Image load error: {}", imageResource.getFilename());
            }
        }

        MemberRegisterCmd cmd = new MemberRegisterCmd(
                email,
                "1234",
                nickname,
                null,
                profileMultipart
        );

        memberService.join(cmd);
    }

    private Resource getProfileImage(List<Resource> images, int index) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.get(index % images.size());
    }

    private boolean isImageFile(String filename) {
        String lowerName = filename.toLowerCase();
        return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".png") || lowerName.endsWith(".gif") ||
                lowerName.endsWith(".webp");
    }

    private List<String> getRealKoreanNicknames() {
        return new ArrayList<>(Arrays.asList(
                "민트초코", "뜨거운아아", "치킨매니아", "탕수육부먹", "탕수육찍먹",
                "제로콜라", "마라탕중독", "삼겹살소주", "치즈볼", "야식금지",
                "월급루팡", "칼퇴요정", "집에가고싶다", "내일은없다", "로또1등",
                "주말언제와", "퇴사꿈나무", "야근싫어", "넵알겠습니다", "자본주의미소",
                "새벽감성", "푸른하늘", "밤편지", "소나기", "구름",
                "별헤는밤", "봄날의햇살", "가을바람", "겨울아이", "바다보러갈래",
                "즐겜러", "빡겜러", "버스기사", "트롤아님", "힐좀주세요",
                "존버성공", "떡상가즈아", "뉴비입니다", "고인물", "천상계",
                "하루", "루나", "카이", "제니", "레오",
                "모모", "소라", "나비", "두부", "호두",
                "뚱냥이", "멍멍이", "아기사자", "토끼대장", "햄찌",
                "오리대장", "곰돌이", "다람쥐", "뱁새", "물개박수",
                "나는생각이없다", "왜그러세요", "지나가던사람", "닉네임추천좀", "배고파요",
                "운동하는남자", "코딩하는여자", "자바를잡아라", "오류투성이", "버그수집가",
                "한라산", "제주도푸른밤", "강남역11번출구", "홍대피플", "한강라이딩",
                "방구석여포", "이불밖은위험해", "넷플릭스중독", "유튜브보는중", "구독좋아요"
        ));
    }

    // 내부에서 사용할 Dummy MultipartFile 클래스
    static class DummyMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFileName;
        private final String contentType;
        private final byte[] content;

        public DummyMultipartFile(Resource resource) throws IOException {
            this.name = "file";
            this.originalFileName = resource.getFilename();
            this.content = FileCopyUtils.copyToByteArray(resource.getInputStream());

            String determinedType = null;
            if (originalFileName != null) {
                determinedType = Files.probeContentType(new File(originalFileName).toPath());
            }
            this.contentType = (determinedType != null) ? determinedType : "application/octet-stream";
        }

        @Override
        @NonNull
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFileName;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        @NonNull
        public byte[] getBytes() {
            return content;
        }

        @Override
        @NonNull
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(@NonNull File dest) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
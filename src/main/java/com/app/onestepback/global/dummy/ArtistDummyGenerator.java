package com.app.onestepback.global.dummy;

import com.app.onestepback.domain.model.MemberVO;
import com.app.onestepback.repository.MemberMapper;
import com.app.onestepback.service.artist.ArtistService;
import com.app.onestepback.service.artist.cmd.ArtistRegisterCMD;
import com.app.onestepback.service.member.MemberService;
import com.app.onestepback.service.member.cmd.MemberRegisterCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArtistDummyGenerator {

    private static final String PROFILE_PATH = "classpath:static/images/dummy/profile/artist*";
    private static final String BANNER_PATH = "classpath:static/images/dummy/banner/banner*";
    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final ArtistService artistService;
    private final ResourcePatternResolver resourcePatternResolver;

    @Transactional
    public void generate() {
        if (memberService.existsByEmail("artist0@test.com")) {
            log.info("Artist dummy data already exists. Skipping execution.");
            return;
        }

        log.info("Starting Elite Artist Squad Generation (20 Units) with Self-Intro...");

        List<Resource> profileImages = loadResources(PROFILE_PATH);
        List<Resource> bannerImages = loadResources(BANNER_PATH);

        if (profileImages.isEmpty() || bannerImages.isEmpty()) {
            log.error("필수 이미지가 없다. 경로 확인해라.\nProfile: {}\nBanner: {}", PROFILE_PATH, BANNER_PATH);
            return;
        }

        List<ArtistPreset> elites = getEliteArtistPresets();

        for (int i = 0; i < elites.size(); i++) {
            ArtistPreset preset = elites.get(i);
            Resource profileRes = getResourceSafe(profileImages, i);
            Resource bannerRes = getResourceSafe(bannerImages, i);

            try {
                // Step A: Member 생성 (introduction 포함)
                Long memberId = createMemberAndGetId(preset, profileRes);

                // Step B: Artist 등록
                createArtist(memberId, preset, bannerRes);

            } catch (Exception e) {
                log.error("Failed to create artist: {}", preset.nickname(), e);
            }
        }

        log.info("Mission Complete: 20 Elite Artists (with Intro) Deployed.");
    }

    // --- Core Logic ---

    private Long createMemberAndGetId(ArtistPreset preset, Resource profileRes) {
        MultipartFile profileMultipart = convertToMultipart(profileRes);

        // [Update] introduction 필드 추가됨
        MemberRegisterCmd cmd = new MemberRegisterCmd(
                preset.email(),
                "1234",
                preset.nickname(),
                preset.memberIntroduction(), // 유저 자기소개 (New)
                profileMultipart
        );
        memberService.join(cmd);

        return memberMapper.selectByEmail(preset.email())
                .map(MemberVO::getId)
                .orElseThrow(() -> new IllegalStateException("ID 조회 실패: " + preset.email()));
    }

    private void createArtist(Long memberId, ArtistPreset preset, Resource bannerRes) {
        MultipartFile bannerMultipart = convertToMultipart(bannerRes);

        ArtistRegisterCMD cmd = new ArtistRegisterCMD(
                memberId,
                preset.blogName(),
                preset.blogDescription(), // 블로그 설명
                bannerMultipart
        );

        artistService.saveArtist(cmd);
    }

    // --- The 20 Legends (Updated Data) ---
    private List<ArtistPreset> getEliteArtistPresets() {
        return List.of(
                // 0
                new ArtistPreset(
                        "artist0@test.com",
                        "Retro_Soul",
                        "오래된 LP판과 턴테이블을 수집합니다.",
                        "Analog_Dreams",
                        "지직거리는 노이즈가 주는 편안함. 8090 시티팝과 올드 재즈 아카이브."
                ),
                // 1
                new ArtistPreset(
                        "artist1@test.com",
                        "Romantic_Walker",
                        "지도 따윈 보지 않습니다. 발길 닿는 곳이 곧 길이니까.",
                        "Old_Town_Road",
                        "붉은 지붕과 거친 돌길, 중세의 시간 속을 걷는 고독한 여행기."
                ),
                // 2
                new ArtistPreset(
                        "artist2@test.com",
                        "낭만_개발자",
                        "차가운 모니터 앞에서 뜨거운 심장으로 코드를 짭니다.",
                        "이진법의_온도",
                        "0과 1 사이에도 감성은 존재한다. 딱딱한 개발 이야기에 사람 냄새 더하기."
                ),
                // 3
                new ArtistPreset(
                        "artist3@test.com",
                        "별빛_방랑자",
                        "현실의 중력을 거스르는 몽환적인 색감을 담습니다.",
                        "꿈의_경계선",
                        "보랏빛 밤하늘과 설산의 침묵. 당신이 잃어버린 꿈을 디지털 캔버스에 복원합니다."
                ),
                // 4
                new ArtistPreset(
                        "artist4@test.com",
                        "빛의_기록자",
                        "셔터가 닫히는 순간, 찰나는 영원이 됩니다.",
                        "24프레임의_기억",
                        "붉은 필름 위로 흐르는 아날로그 감성. 편집되지 않은 날 것 그대로의 삶을 상영합니다."
                ),
                // 5
                new ArtistPreset(
                        "artist5@test.com",
                        "태양의_순례자",
                        "숨이 턱 끝까지 차오르는 순간, 비로소 살아있음을 느낍니다.",
                        "불타는_지평선",
                        "문명이 닿지 않은 곳. 험준한 산맥과 붉은 노을이 빚어내는 대자연의 서사시."
                ),
                // 6
                new ArtistPreset(
                        "artist6@test.com",
                        "펜_끝의_창조주",
                        "상상을 현실로 만드는 시간, 24프레임.",
                        "Frame_By_Frame",
                        "단 1초의 감동을 위해 수천 장을 그립니다. 애니메이션 제작의 땀과 열정."
                ),
                // 7
                new ArtistPreset(
                        "artist7@test.com",
                        "자연의_목격자",
                        "셔터 찬스는 기다리는 자에게만 찾아옵니다.",
                        "반영의_미학",
                        "붉은 단풍과 호수 위로 비치는 설산의 침묵. 대자연의 절경을 고화질로 기록합니다."
                ),
                // 8
                new ArtistPreset(
                        "artist8@test.com",
                        "항구의_보헤미안",
                        "집은 없습니다. 발길 닿는 곳이 곧 나의 아틀리에.",
                        "길_위의_팔레트",
                        "알록달록한 항구의 활기와 낯선 도시의 소음. 지도 대신 붓을 들고 기록하는 세상의 모든 색."
                ),
                // 9
                new ArtistPreset(
                        "artist9@test.com",
                        "여백의_연금술사",
                        "빈 종이는 두렵지 않습니다. 채워질 가능성이니까.",
                        "Atelier_No.9",
                        "거친 종이의 질감과 붓의 흐름이 만나는 순간. 흑백의 대비 속에 숨겨진 색채의 미학을 탐구합니다."
                ),
                // 10
                new ArtistPreset(
                        "artist10@test.com",
                        "Urban_Jazz",
                        "색소폰 연주자입니다. 즉흥 연주의 매력을 전합니다.",
                        "Blue_Rhythm",
                        "도심 속의 재즈 라이브. 정통 재즈부터 펑크(Funk)까지 다양한 그루브를 기록합니다."
                ),
                // 11
                new ArtistPreset(
                        "artist11@test.com",
                        "space_maestro",
                        "건축은 얼어붙은 음악입니다. 선과 면으로 세상을 조율하죠.",
                        "Structure_Log",
                        "빛과 그림자, 그리고 기하학의 변주. 인간이 머무는 공간에 대한 구조적 미학을 설계합니다."
                ),
                // 12
                new ArtistPreset(
                        "artist12@test.com",
                        "비트_스미스",
                        "말보다는 리듬으로 대화합니다. 스틱이 부러질 때까지.",
                        "Neon_Groove",
                        "화려한 조명 아래 흐르는 땀방울과 스틱의 파열음. 심박수를 최대치로 끌어올리는 드럼 세션 기록."
                ),
                // 13
                new ArtistPreset(
                        "artist13@test.com",
                        "주파수_조각가",
                        "눈에 보이지 않는 공기의 떨림을 다듬습니다.",
                        "Sound_Wave_Lab",
                        "침묵과 소음 사이, 완벽한 균형을 찾아서. 세상의 모든 소리를 기록하고 편집합니다."
                ),
                // 14
                new ArtistPreset(
                        "artist14@test.com",
                        "스케일_마이스터",
                        "플라스틱 덩어리에 숨결을 불어넣습니다. 디테일이 곧 생명.",
                        "The_Hangar_No.1",
                        "접착제 냄새와 에어브러시의 소음. 1/48 스케일 속에 거대한 역사를 재현하는 프라모델 제작소."
                ),
                // 15
                new ArtistPreset(
                        "artist15@test.com",
                        "작은_세상의_신",
                        "현실은 통제할 수 없지만, 내 책상 위 세상은 완벽합니다.",
                        "Micro_Universe",
                        "1/64 스케일로 압축된 삶의 풍경. 손톱만 한 사람들의 표정 속에 숨겨진 이야기를 조각합니다."
                ),
                // 16
                new ArtistPreset(
                        "artist16@test.com",
                        "Curve_Master",
                        "속도를 조각합니다. 멈춰있을 때도 달려나가는 것처럼.",
                        "Sketch_to_Reality",
                        "종이 위의 스케치가 실제 차량이 되기까지. 빛과 공기의 흐름을 고려한 자동차 디자인 프로세스와 철학을 공유합니다."
                ),
                // 17
                new ArtistPreset(
                        "artist17@test.com",
                        "심도의_지배자",
                        "뷰파인더 너머의 세상만이 진실입니다.",
                        "Deep_Focus",
                        "82mm 렌즈가 포착하는 빛과 어둠의 경계. 일상을 누아르 영화처럼 기록하는 시네마틱 포토그래피."
                ),
                // 18
                new ArtistPreset(
                        "artist18@test.com",
                        "접시_위의_설계자",
                        "맛은 혀끝이 아니라 눈에서 시작됩니다.",
                        "Gourmet_Canvas",
                        "재료의 본질을 살리는 조리법과 감각적인 플레이팅. 오감을 지배하는 파인 다이닝의 미학."
                ),
                // 19
                new ArtistPreset(
                        "artist19@test.com",
                        "Bespoke_Master",
                        "수트는 남자의 갑옷입니다. 단 1mm의 오차도 허용하지 않죠.",
                        "The_Classic_Cut",
                        "유행은 사라지지만 클래식은 영원합니다. 거친 원단이 완벽한 핏으로 재탄생하는 비스포크 테일러링의 정수."
                )
        );
    }

    private List<Resource> loadResources(String path) {
        try {
            Resource[] resources = resourcePatternResolver.getResources(path);
            return Arrays.stream(resources)
                    .filter(Resource::exists)
                    .filter(r -> isImageFile(r.getFilename()))
                    .sorted(Comparator.comparing(Resource::getFilename))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("Resource loading failed: {}", path);
            return Collections.emptyList();
        }
    }

    // --- Utilities ---

    private Resource getResourceSafe(List<Resource> list, int index) {
        if (list.isEmpty()) return null;
        return list.get(index % list.size());
    }

    private MultipartFile convertToMultipart(Resource resource) {
        if (resource == null || !resource.exists()) return null;
        try {
            return new DummyMultipartFile(resource);
        } catch (IOException e) {
            return null;
        }
    }

    private boolean isImageFile(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".png") || lower.endsWith(".jpeg") || lower.endsWith(".webp");
    }

    // [Update] Record Field Added: memberIntroduction
    record ArtistPreset(
            String email,
            String nickname,
            String memberIntroduction, // New: 유저 자기소개
            String blogName,
            String blogDescription     // Old: 블로그 설명
    ) {
    }

    static class DummyMultipartFile implements MultipartFile {
        private final String name = "file";
        private final String originalFileName;
        private final String contentType;
        private final byte[] content;

        public DummyMultipartFile(Resource resource) throws IOException {
            this.originalFileName = resource.getFilename();
            this.content = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String type = null;
            if (originalFileName != null) type = Files.probeContentType(new File(originalFileName).toPath());
            this.contentType = (type != null) ? type : "application/octet-stream";
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
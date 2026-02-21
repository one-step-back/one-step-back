//package com.app.onestepback.global.dummy;
//
//import com.app.onestepback.domain.model.MemberVO;
//import com.app.onestepback.domain.type.feed.FeedCategory;
//import com.app.onestepback.domain.type.feed.FeedStatus;
//import com.app.onestepback.repository.MemberMapper;
//import com.app.onestepback.service.feed.FeedService;
//import com.app.onestepback.service.feed.cmd.FeedWriteCmd;
//import com.app.onestepback.service.file.FileInfo;
//import com.app.onestepback.service.file.FileService;
//import com.app.onestepback.service.file.cmd.SaveFileCmd;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class FeedDummyGenerator {
//
//    // 이미지 경로 루트
//    private static final String FEED_IMG_ROOT = "classpath:static/images/dummy/feed/";
//    private final FeedService feedService;
//    private final FileService fileService;
//    private final MemberMapper memberMapper;
//    private final ResourcePatternResolver resourcePatternResolver;
//
//    @Transactional
//    public void generate() {
//        log.info("Starting Feed Generation for 20 Artists...");
//
//        // 1. 아티스트별 피드 시나리오 로드
//        Map<String, List<FeedScenario>> scenarioMap = getFeedScenarios();
//
//        // 2. 아티스트 루프
//        for (String email : scenarioMap.keySet()) {
//            MemberVO member = memberMapper.selectByEmail(email).orElse(null);
//            if (member == null) {
//                log.warn("Artist not found: {}. Skipping.", email);
//                continue;
//            }
//
//            List<FeedScenario> scenarios = scenarioMap.get(email);
//            for (FeedScenario scenario : scenarios) {
//                try {
//                    processFeed(member.getId(), scenario);
//                } catch (Exception e) {
//                    log.error("Failed to write feed for {}: {}", email, scenario.title, e);
//                }
//            }
//        }
//        log.info("Feed Generation Complete.");
//    }
//
//    @SneakyThrows
//    private void processFeed(Long artistId, FeedScenario scenario) {
//        List<String> fileIds = new ArrayList<>();
//
//        // 1. 파일이 있다면 업로드 처리
//        if (scenario.imageFiles != null && !scenario.imageFiles.isEmpty()) {
//            for (String filename : scenario.imageFiles) {
//                String location = FEED_IMG_ROOT + filename;
//                try {
//                    Resource resource = resourcePatternResolver.getResource(location);
//                    if (!resource.exists()) {
//                        log.warn("File not found: {}. Proceeding without this file.", location);
//                        continue;
//                    }
//
//                    SaveFileCmd saveFileCmd = new SaveFileCmd(
//                            artistId,
//                            null,
//                            "FEED",
//                            resource.getFilename(),
//                            determineContentType(resource.getFilename()),
//                            resource.contentLength(),
//                            () -> {
//                                try {
//                                    return resource.getInputStream();
//                                } catch (IOException e) {
//                                    throw new RuntimeException("InputStream 생성 실패: " + filename, e);
//                                }
//                            }
//                    );
//
//                    // 파일 저장 및 ID 획득
//                    FileInfo fileInfo = fileService.store(saveFileCmd);
//                    fileIds.add(fileInfo.id());
//
//                } catch (IOException e) {
//                    log.error("Error processing file: {}", filename, e);
//                }
//            }
//        }
//
//        // 2. 피드 발행 커맨드 생성
//        FeedWriteCmd cmd = new FeedWriteCmd(
//                artistId,
//                scenario.title,
//                scenario.content,
//                scenario.category,
//                FeedStatus.PUBLIC,
//                fileIds
//        );
//
//        // 3. 서비스 호출
//        feedService.write(cmd);
//    }
//
//    private String determineContentType(String filename) {
//        if (filename == null) return "application/octet-stream";
//        String lower = filename.toLowerCase();
//        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
//        if (lower.endsWith(".png")) return "image/png";
//        if (lower.endsWith(".webp")) return "image/webp";
//        if (lower.endsWith(".mp4")) return "video/mp4";
//        return "application/octet-stream";
//    }
//
//    // --- Data Definition ---
//
//    private Map<String, List<FeedScenario>> getFeedScenarios() {
//        Map<String, List<FeedScenario>> map = new HashMap<>();
//
//        // Helper to add easily
//        GenerateHelper helper = new GenerateHelper(map);
//
//        // --- 0. Retro_Soul (LP/Jazz) ---
//        helper.add("artist0@test.com", "오늘의 수집품",
//                "운 좋게 구한 1980년대 시티팝 초판 LP. 노이즈마저 음악의 일부다.",
//                FeedCategory.DAILY, List.of("feed_0_1.png"));
//        helper.add("artist0@test.com", "새벽의 작업실 : Jazz Analysis",
//                """
//                        Chet Baker의 트럼펫 솔로 구간을 분석하고 있습니다. 단순히 음을 뱉는 게 아니라, 침묵을 연주하는 경지.\s
//
//                        이번 주말 매거진에 기고할 칼럼 주제는 '비와 고독, 그리고 재즈'로 정했습니다.\s
//                        창밖의 빗소리와 바이닐(Vinyl)의 노이즈가 섞이며 완벽한 백색 소음을 만들어주는군요. 집중하기 좋은 밤입니다.""",
//                FeedCategory.WORK, null);
//
//        // --- 1. Romantic_Walker (Travel/Cobblestone) ---
//        helper.add("artist1@test.com", "로텐부르크의 아침",
//                "지도 없이 걷다가 마주친 풍경. 이 붉은 지붕들이 들려주는 이야기가 있다.",
//                FeedCategory.DAILY, List.of("feed_1_1.png"));
//
//        // --- 2. 낭만_개발자 (Code/Sentiment) ---
//        helper.add("artist2@test.com", "새벽 2시의 코딩",
//                "커피는 식었고, 에러 로그는 붉다. 하지만 빌드 성공(Build Success) 메시지 하나에 모든 피로가 씻겨나간다.",
//                FeedCategory.FREE, List.of("feed_2_1.jpg"));
//
//        // --- 3. 별빛_방랑자 (Digital Art) ---
//        helper.add("artist3@test.com", "Dreamscape No.4",
//                "꿈에서 본 설산을 디지털 캔버스에 옮겼습니다. 보랏빛 하늘이 주는 위로.",
//                FeedCategory.ART, List.of("feed_3_1.jpg"));
//
//        // --- 4. 빛의_기록자 (Analog Photo) ---
//        helper.add("artist4@test.com", "필름이 감기는 소리",
//                "디지털 카메라는 결과를 보여주지만, 필름 카메라는 과정을 선물한다. 현상소를 나설 때의 설렘.",
//                FeedCategory.ART, List.of("feed_4_1.jpg"));
//
//        // --- 5. 태양의_순례자 (Nature/Mountain) ---
//        helper.add("artist5@test.com", "정상에 서다",
//                "숨이 턱 끝까지 차오르는 고통 뒤에 마주한 일출. 이 맛에 산을 탄다.",
//                FeedCategory.FREE, List.of("feed_5_1.jpg"));
//
//        // --- 6. 펜_끝의_창조주 (Animation) ---
//        helper.add("artist6@test.com", "Storyboard_Final",
//                "이번 씬의 핵심은 유성우가 떨어지는 타이밍. 초당 24프레임의 마법을 믿는다.",
//                FeedCategory.ART, List.of("feed_6_1.jpg"));
//
//        // --- 7. 자연의_목격자 (Landscape Photo) ---
//        helper.add("artist7@test.com", "반영(Reflection)",
//                "호수 위로 비친 또 하나의 세상. 바람이 멈추기를 2시간 기다려 얻은 컷.",
//                FeedCategory.ART, List.of("feed_7_1.jpg"));
//
//        // --- 8. 항구의_보헤미안 (Wandering Art) ---
//        helper.add("artist8@test.com", "코펜하겐의 색",
//                "이 도시의 건물들은 마치 팔레트 같다. 오늘은 뉘하운 항구에 앉아 스케치를 남긴다.",
//                FeedCategory.ART, List.of("feed_8_1.jpg")),
//
//        // --- 9. 여백의_연금술사 (Fine Art) ---
//        helper.add("artist9@test.com", "질감의 탐구",
//                "거친 붓 자국 위에 덧입혀지는 화이트. 비움으로써 채워지는 것들에 대하여.",
//                FeedCategory.ART, List.of("feed_9_1.jpg"));
//
//        // --- 10. Urban_Jazz (Saxophone) ---
//        helper.add("artist10@test.com", "Blue Note",
//                "오늘 연주는 즉흥(Improvisation)이 8할이었다. 관객과의 호흡이 만들어낸 그루브.",
//                FeedCategory.ART, List.of("feed_10_1.jpg"));
//
//        // --- 11. 공간_지휘자 (Architecture) ---
//        helper.add("artist11@test.com", "선의 미학",
//                "완벽한 대칭이 주는 안정감. 건축물은 빛을 담는 그릇이다.",
//                FeedCategory.ART, List.of("feed_11_1.jpg"));
//
//        // --- 12. 비트_스미스 (Drum) ---
//        helper.add("artist12@test.com", "Sound Check",
//                "스네어 드럼의 텐션을 조금 더 조였다. 심장을 때리는 소리가 날 때까지.",
//                FeedCategory.ART, List.of("feed_12_1.jpg"));
//
//        // --- 13. 주파수_조각가 (Sound Engineering) ---
//        helper.add("artist13@test.com", "Waveform",
//                "눈으로 보는 소리. 이 파형 안에 가수의 숨소리와 감정이 모두 담겨있다.",
//                FeedCategory.ART, List.of("feed_13_1.jpg"));
//
//        // --- 14. 스케일_마이스터 (Plastic Model) ---
//        helper.add("artist14@test.com", "웨더링 작업 중",
//                "전장의 흙먼지를 표현하는 중. 너무 과하지 않게, 딱 그 시절의 느낌으로.",
//                FeedCategory.FREE, List.of("feed_14_1.jpg"));
//
//        // --- 15. 작은_세상의_신 (Diorama) ---
//        helper.add("artist15@test.com", "쇼와 시대의 골목",
//                "1/64 스케일로 재현한 1960년대. 저 가로등에 불이 들어오는 순간, 시간여행이 시작된다.",
//                FeedCategory.ART, List.of("feed_15_1.jpg"));
//
//        // --- 16. Curve_Master (Car Design) ---
//        helper.add("artist16@test.com", "Clay Modeling",
//                "2D 스케치를 3D로 옮기는 시간. 손끝으로 곡선을 느끼며 공기역학을 조각한다.",
//                FeedCategory.ART, List.of("feed_16_1.jpg"));
//
//        // --- 17. 심도의_지배자 (Cinematic Photo) ---
//        helper.add("artist17@test.com", "Through the Lens",
//                "82mm 렌즈가 바라보는 세상. 피사체 외의 모든 소음은 아웃포커싱으로 날려버린다.",
//                FeedCategory.ART, List.of("feed_17_1.jpg"));
//
//        // --- 18. 접시_위의_설계자 (Plating) ---
//        helper.add("artist18@test.com", "Spring on Plate",
//                "제철 식재료가 가진 색감을 최대한 살렸다. 눈으로 먼저 맛보는 봄.",
//                FeedCategory.ART, List.of("feed_18_1.jpg"));
//
//        // --- 19. Bespoke_Master (Tailor) ---
//        helper.add("artist19@test.com", "가봉(Basting)",
//                "수트가 완성되기 전, 가장 중요한 순간. 고객의 체형을 읽어내는 시간.",
//                FeedCategory.ART, List.of("feed_19_1.jpg"));
//
//        return map;
//    }
//
//    record FeedScenario(
//            String title,
//            String content,
//            FeedCategory category,
//            List<String> imageFiles // null이면 텍스트 피드
//    ) {
//    }
//
//    // Helper Class to reduce boilerplate
//    static class GenerateHelper {
//        private final Map<String, List<FeedScenario>> map;
//
//        GenerateHelper(Map<String, List<FeedScenario>> map) {
//            this.map = map;
//        }
//
//        void add(String email, String title, String content, FeedCategory category, List<String> images) {
//            map.computeIfAbsent(email, k -> new ArrayList<>())
//                    .add(new FeedScenario(title, content, category, images));
//        }
//    }
//}
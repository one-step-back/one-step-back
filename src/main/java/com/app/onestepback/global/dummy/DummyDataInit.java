package com.app.onestepback.global.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("dev")
public class DummyDataInit {

    private final MemberDummyGenerator memberDummyGenerator;
    private final ArtistDummyGenerator artistDummyGenerator;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initData() {
        log.info("========== Starting Dummy Data Initialization ==========");

        memberDummyGenerator.generate();

         artistDummyGenerator.generate();

        log.info("========== Dummy Data Initialization Completed ==========");
    }
}
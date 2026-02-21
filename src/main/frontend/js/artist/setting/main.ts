/**
 * @file main.ts
 * @package artist/setting
 * @description 아티스트 설정 페이지의 탭 전환 로직을 제어하고, 각 하위 모듈(정보, 계좌, 멤버십)을 초기화하는 엔트리 포인트입니다.
 */

import $ from 'jquery';
import { initArtistTab } from './artist';
import { initAccountTab, loadAccountInfo } from './account';
import { initMembershipTab, loadMembershipData } from './membership';

$(() => {
    // ===================================================================================
    //  SECTION: Module Initialization
    // ===================================================================================

    initArtistTab();
    initAccountTab();
    initMembershipTab();

    // ===================================================================================
    //  SECTION: Tab Navigation Logic
    // ===================================================================================

    const $tabs = $('.tab-item');
    const $contents = $('.tab-content');

    $tabs.on('click', function (e: JQuery.ClickEvent) {
        const $target = $(e.currentTarget);
        const tabName = $target.data('tab') as string;

        $tabs.removeClass('active');
        $target.addClass('active');

        $contents.removeClass('active');
        $('#tab-' + tabName).addClass('active');

        // 히스토리 조작 시 브라우저 기본 동작 충돌 방지
        try {
            history.replaceState(null, '', '#' + tabName);
        } catch (err) {
            console.error("Failed to replace history state:", err);
        }

        // 각 탭에 진입할 때마다 최신 데이터를 로드하도록 이벤트를 분배합니다.
        if (tabName === 'account') {
            loadAccountInfo();
        } else if (tabName === 'membership') {
            loadMembershipData();
        }
    });

    // ===================================================================================
    //  SECTION: Bootstrap & Deep Linking
    // ===================================================================================

    // URL 해시에 따른 탭 자동 선택 로직
    const hash = window.location.hash.replace('#', '');
    if (hash && $(`.tab-item[data-tab="${hash}"]`).length > 0) {
        $(`.tab-item[data-tab="${hash}"]`).trigger('click');
    } else {
        if (!$('.tab-item.active').length) {
            $('.tab-item').first().trigger('click');
        }
    }
});
/**
 * @file alert.ts
 * @description 전역 알림(Alert) 모달 모듈
 */

import $ from "jquery"

export const Alert = {
    $overlay: null as JQuery | null,
    $title: null as JQuery | null,
    $desc: null as JQuery | null,
    $actionBtn: null as JQuery | null,

    /** UI 요소 초기화 및 이벤트 바인딩 */
    init() {
        this.$overlay = $('#global-alert-dialog');
        this.$title = $('#alert-title');
        this.$desc = $('#alert-description');
        this.$actionBtn = $('#alert-action-btn');

        $('#alert-cancel-btn').on('click', () => this.hide());
        this.$overlay.on('click', (e) => {
            if (e.target === this.$overlay![0]) this.hide();
        });
    },

    /**
     * 알림창을 띄우고 확인 시 콜백을 실행합니다.
     * @param title 제목
     * @param desc 내용
     * @param onConfirm 확인 클릭 시 실행할 함수
     */
    confirm(title: string, desc: string, onConfirm: () => void) {
        if (!this.$overlay) this.init();
        this.$title!.text(title);
        this.$desc!.text(desc);
        this.$actionBtn!.off('click').on('click', () => {
            onConfirm();
            this.hide();
        });
        this.$overlay!.fadeIn(200).css('display', 'flex');
    },

    /** 모달 숨김 처리 */
    hide() {
        if (this.$overlay) this.$overlay.fadeOut(200);
    }
};
/**
 * @file confirm.ts
 * @description 사용자 확답(Confirm) 모달 모듈
 */
import $ from "jquery"

interface ConfirmOptions {
    title?: string;
    desc?: string;
    actionText?: string;
    onConfirm: () => void;
}

export const Confirm = {
    $overlay: null as JQuery | null,

    /** 초기화 */
    init() {
        this.$overlay = $('#global-confirm-modal');
        $('#confirm-cancel-btn').on('click', () => this.hide());
    },

    /**
     * 유저의 확답을 받아내는 모달을 오픈합니다.
     * @param options title, desc, actionText, onConfirm
     */
    open({title, desc, actionText, onConfirm}: ConfirmOptions) {
        if (!this.$overlay) this.init();

        $('#confirm-title').text(title || '확인');
        $('#confirm-description').text(desc || '계속하시겠습니까?');
        if (actionText) $('#confirm-action-btn').text(actionText);

        $('#confirm-action-btn').off('click').on('click', () => {
            onConfirm();
            this.hide();
        });

        this.$overlay!.css('display', 'flex').hide().fadeIn(200);
    },

    hide() {
        if (this.$overlay) this.$overlay.fadeOut(200);
    }
};
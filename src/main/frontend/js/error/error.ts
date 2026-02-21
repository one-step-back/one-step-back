/**
 * @file error.ts
 * @description 403, 404, 500 에러 페이지의 뒤로 가기 및 새로고침 이벤트를 제어합니다.
 */

import $ from 'jquery';

$(() => {
    // 이전 페이지로 돌아가기 로직 (403, 404)
    $('#btn-back').on('click', () => {
        if (window.history.length > 1) {
            window.history.back();
        } else {
            window.location.href = '/';
        }
    });

    // 페이지 새로고침 로직 (500)
    $('#btn-reload').on('click', () => {
        window.location.reload();
    });
});
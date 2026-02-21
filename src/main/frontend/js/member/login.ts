/**
 * @file login.ts
 * @package member
 * @description 로그인 페이지 진입 시 URL 파라미터를 분석하여 사용자에게 피드백(토스트)을 제공하는 모듈
 */

import $ from 'jquery';
import { Show } from '../util/toast';

$(() => {
    /** * @constant {URLSearchParams} urlParams
     * 브라우저 주소창의 쿼리 스트링(Query String)을 파싱한 객체
     */
    const urlParams = new URLSearchParams(window.location.search);

    /**
     * @function handleUrlStatus
     * @description 특정 파라미터 존재 여부에 따라 성공/에러 토스트를 노출하고 URL을 정화함
     */
    const handleUrlStatus = (): void => {

        /** * [CASE 1] 회원가입 완료 후 리다이렉트 된 경우
         * @param {string} join - 'true'일 때 가입 축하 메시지 노출
         */
        if (urlParams.get('join') === 'true') {
            Show.success("회원가입 완료! 이제 로그인을 진행해주세요.");
            clearUrlParams();
        }

        /** * [CASE 2] 접근 권한이 없는 페이지에서 튕겨져 온 경우
         * @param {string} need-login - 'true'일 때 경고 메시지 노출
         */
        if (urlParams.get('need-login') === 'true') {
            Show.error("로그인이 필요한 서비스입니다.");
            clearUrlParams();
        }
    };

    /**
     * @function clearUrlParams
     * @description History API를 이용하여 URL의 쿼리 파라미터를 제거함 (새로고침 시 중복 알림 방지)
     * @note TS2774 에러 해결: 존재가 확실한 함수에 대한 불필요한 조건부 체크 삭제
     */
    const clearUrlParams = (): void => {
        history.replaceState({}, '', window.location.pathname);
    };

    /** @section Execution - 로직 즉시 실행 */
    handleUrlStatus();
});
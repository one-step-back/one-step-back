/**
 * @file timeUtil.ts
 * @description 날짜 데이터를 상대 시간(예: "3분 전")으로 변환하는 유틸리티 모듈입니다.
 */

/**
 * @function timeAgo
 * @description 날짜 문자열 또는 Spring Boot의 LocalDateTime 배열을 상대 시간 문자열로 변환합니다.
 * @param {string | number[]} dateData ISO 포맷 날짜 문자열 또는 숫자 배열
 * @returns {string} 포맷팅된 상대 시간 문자열
 */
export function timeAgo(dateData: string | number[]): string {
    let date: Date;

    if (Array.isArray(dateData)) {
        date = new Date(dateData[0], dateData[1] - 1, dateData[2], dateData[3] || 0, dateData[4] || 0, dateData[5] || 0);
    } else {
        date = new Date(dateData);
    }

    const seconds = Math.floor((new Date().getTime() - date.getTime()) / 1000);
    if (seconds < 60) return "방금 전";

    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) return `${minutes}분 전`;

    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}시간 전`;

    const days = Math.floor(hours / 24);
    if (days < 7) return `${days}일 전`;

    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, "0")}.${String(date.getDate()).padStart(2, "0")}`;
}
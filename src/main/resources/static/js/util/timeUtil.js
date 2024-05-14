export function getTimeGapFromNow(datetime) {
    const now = new Date();
    const date = new Date(datetime);
    let gap = Math.floor((now.getTime() - date.getTime()) / 1000 / 60);

    if (gap < 1) {
        return "방금 전";
    }
    if (gap < 60) {
        return `${gap}분 전`;
    }

    gap = Math.floor(gap / 60);
    if (gap < 24) {
        return `${gap}시간 전`;
    }

    gap = Math.floor(gap / 24);
    if (gap < 32) {
        return `${gap}일 전`;
    }

    gap = Math.floor(gap / 31);
    if (gap < 13) {
        return `${gap}개월 전`;
    }

    gap = Math.floor(gap / 12);
    return `${gap}년 전`;
}
/**
 * @file toast.ts
 * @description 실시간 토스트 메시지 시스템
 */

export const Show = {
    stack: [] as HTMLElement[],

    /** 토스트 컨테이너 생성 및 메시지 발사 */
    _fire(message: string, type: 'success' | 'error' | 'info', iconClass: string) {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast-item entering ${type}`;
        toast.innerHTML = `<i class="${iconClass} toast-icon"></i><span>${message}</span>`;

        this.stack.unshift(toast);
        container.appendChild(toast);
        this._update();

        setTimeout(() => toast.classList.remove('entering'), 300);
        setTimeout(() => this.remove(toast), 4000);
    },

    /** 특정 토스트 제거 */
    remove(target: HTMLElement) {
        target.style.opacity = '0';
        target.style.transform = 'translateY(10px) scale(0.95)';

        setTimeout(() => {
            this.stack = this.stack.filter(t => t !== target);
            target.remove();
            this._update();
        }, 300);
    },

    /** 스택 순서에 따른 데이터 인덱스 업데이트 */
    _update() {
        this.stack.forEach((toast, i) => {
            toast.setAttribute('data-index', (i > 3 ? 3 : i).toString());
        });
    },

    success(m: string) {
        this._fire(m, 'success', 'fas fa-check-circle');
    },
    error(m: string) {
        this._fire(m, 'error', 'fas fa-exclamation-circle');
    },
    info(m: string) {
        this._fire(m, 'info', 'fas fa-info-circle');
    }
};
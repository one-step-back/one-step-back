const blogModal = $("#b-modal");
const blogModalLink = $("#b-modal-link-container");

$(document).ready(function() {
    linkBtn();

    $(".btn-share").click(()=>{
        blogModal.show()
        blogModalLink.show()
    })

    $(".b-modal-btn").click(function() {
        modalClose();
    });

    // "닫기" 버튼에 대한 클릭 이벤트 핸들러
    $(".b-btn-close").click(function() {
        modalClose();
    });
});

// 링크 공유 버튼 함수
function linkBtn(){
    let currentURL = window.location.href;

    let $inputElement = $(".b-input");
    $inputElement.val(currentURL);

    let $copyButton = $(".b-btn-clipboard");
    $copyButton.click(function() {
        $inputElement.select();
        document.execCommand("copy");
    });
}

function modalClose(){
    blogModal.hide();
    blogModalLink.hide();
}
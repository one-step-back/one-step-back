$(function () {
    $(".blog-action-subscription-btn").click(function () {
        $(this).toggleClass("active");
    });

    // 모달
    $(".blog-action-share").click(function (e) {
        e.preventDefault();
        $(".modal").fadeIn(100);
        $(".modal-container").fadeIn(100);
    });

    $(".btn-close, #modal-alert-confirm").click(function () {
        $(".modal").fadeOut(100);
        $(".modal-container").fadeOut(100);
    });
})
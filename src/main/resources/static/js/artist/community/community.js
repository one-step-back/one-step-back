$(function () {
    $(".blog-action-subscription-btn").click(function () {
        $(this).toggleClass("active");
    });

    // 액션창 띄우기
    $(".post-dropdown").click(function () {
        var $container = $(this).closest(".post-action");
        var $dropdown = $container.find(".action-dropdown-container");

        $dropdown.toggle();
    });

    // 북마크
    $(".post-bookmark").click(function () {
        var $icons = $(this).find("svg");

        $icons.toggleClass("active");
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
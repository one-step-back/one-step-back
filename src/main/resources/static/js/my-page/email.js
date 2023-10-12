$(function () {
    $(".summit-btn").click(function () {
        $(".modal").fadeIn(100);
        $(".modal-container").fadeIn(100);
    });

    $(".btn-close, #modal-alert-confirm").click(function () {
        $(".modal").fadeOut(100);
        $(".modal-container").fadeOut(100);
    });
});
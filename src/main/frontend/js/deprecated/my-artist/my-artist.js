$(function () {
    $(".artist-info-container").click(function (event) {
        event.stopPropagation();
        let $subscriptedArtist = $(this).closest(".subscripted-artist");
        let $artistPostsContainer = $subscriptedArtist.find(".artist-posts-container");

        $artistPostsContainer.slideToggle(200);
    });

    $(".artist-subscription-btn").click(function (event) {
        event.stopPropagation();

        let $subscriptionBtn = $(this);
        let $subscriptionSpans = $subscriptionBtn.find(".artist-subscription-span");
        let $activeText = $subscriptionBtn.find(".active-text");

        if ($subscriptionBtn.hasClass("active")) {
            $subscriptionBtn.removeClass("active");
        } else {
            $subscriptionBtn.addClass("active");
        }

        $subscriptionSpans.toggle();
        $activeText.toggle();
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

});
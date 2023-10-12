$(function () {
    // 드랍다운 쿼리
    $(".head-dropdown").click(function () {
        $(".head-dropdown-menu").toggleClass("active");
    });

    $(".dropdown-menu-a").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        let selectedMenuItem = $(this);
        let selectedText = selectedMenuItem.text();

        $(".orderby-status").text(selectedText);
        $(".dropdown-menu-a").removeClass("active");
        selectedMenuItem.addClass("active");
        $(".head-dropdown-menu").removeClass("active");
    });

    $(document).on("click", function (e) {
        if (!$(e.target).closest(".head-dropdown").length) {
            $(".head-dropdown-menu").removeClass("active");
        }
    });

    // 구독버튼 쿼리
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
});
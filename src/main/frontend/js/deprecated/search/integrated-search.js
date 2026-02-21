$(function () {
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

  //   모달 관리 쿼리
  let $modal = $(".modal");
  let $modalContainer = $(".modal-container");

  function closeModal() {
    $modal.removeClass("show");
  }

  $(".head-filter-btn").click(function (event) {
    event.stopPropagation();
    $modal.addClass("show");
  });

  $modalContainer.click(function (event) {
    event.stopPropagation();
  });

  $(".modal-close-btn").click(function () {
    closeModal();
  });

  $(document).click(function () {
    closeModal();
  });

//  필터 관리 쿼리
  $(".mobo-checkbox-container").click(function () {
    $(this).toggleClass("active");
  });

  // 초기화 버튼
  $(".mofo-reset-btn").click(function() {
    $(".mobo-checkbox-container.active").removeClass("active");
    $(".mobo-checkbox-container").each(function() {
      if ($(this).find("label").text() === "제목" || $(this).find("label").text() === "부제목" || $(this).find("label").text() === "태그" || $(this).find("label").text() === "닉네임") {
        $(this).addClass("active");
      }
    });
  });

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

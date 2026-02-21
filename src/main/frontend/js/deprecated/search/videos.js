$(function () {
  // 지우기버튼 클릭시 지워짐
  $(".search-bar-input-erase-btn").on("click", function () {
    $(".search-bar-input").val("").focus();
    $(this).hide();
  });

  $(".video-action-btn").click(function () {
    var $container = $(this).closest(".video-info-body");
    var $dropdown = $container.find(".action-dropdown-container");

    $dropdown.toggle();
  });

  $(".video-bookmark-btn").click(function () {
    var $icons = $(this).find("svg");

    $icons.toggleClass("active");
  });

  // 모달
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

  // 필터 쿼리
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

  // 드랍다운
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
});

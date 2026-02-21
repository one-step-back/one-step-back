$(function () {
  // 검색창에 값 입력시 지우기 버튼 출력 여부 결정
  $(".search-bar-input").on("input", function () {
    if ($(this).val().trim() !== "") {
      $(".search-bar-input-erase-btn").show();
    } else {
      $(".search-bar-input-erase-btn").hide();
    }
  });

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
});

let modalButton = document.querySelectorAll("button[name=modalbutton]");
let modal = document.getElementById("modal");
let modalcancel = document.getElementById("modal-cancel");
let modalok = document.getElementById("modal-ok");

    // 모달 열기 버튼 클릭 시 모달을 표시.
    modalButton.forEach((button) => {
        button.addEventListener("click", function () {
            modal.style.display = "block";
        });
    });

    // 취소 버튼 클릭 시.
    modalcancel.addEventListener("click", function () {
        modal.style.display = "none";
    });

    // 백단 작업하기 전까지 확인 버튼도 display none
    modalok.addEventListener("click", function () {
        modal.style.display = "none";
    });


// //카테고리 호버
// let categoryhober = document.getElementById(categoryhober);
// let hobercate = document.getElementById(hobercate);
//
//
// // 카테고리 버튼 클릭 시 모달을 표시.
// categoryhober.forEach((button) => {
//     button.addEventListener("click", function () {
//         hobercate.style.display = "block";
//     });
// });

$(function () {
    // 드랍다운 쿼리
    $(".head-dropdown1").click(function () {
        $(".head-dropdown-menu").toggleClass("move");
    });

    $(".header-popover-content-font").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        let selectedMenuItem = $(this);
        let selectedText = selectedMenuItem.text();

        $(".orderby-status").text(selectedText);
        $(".header-popover-content-font").removeClass("move");
        selectedMenuItem.addClass("move");
        $(".head-dropdown-menu").removeClass("move");
    });

    $(document).on("click", function (e) {
        if (!$(e.target).closest(".head-dropdown1").length) {
            $(".head-dropdown-menu").removeClass("move");
        }
    });

});
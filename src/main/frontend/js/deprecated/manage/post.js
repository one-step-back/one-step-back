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





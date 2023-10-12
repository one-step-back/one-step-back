document.addEventListener("DOMContentLoaded", function () {
    const body = document.querySelector("body");
    const bookmarkButtons = document.querySelectorAll(".fa-bookmark");
    const ellipsisButtons = document.querySelectorAll(".fa-ellipsis-h");
    const dropdownMenuRights = document.querySelectorAll(".dropdown-menu-right");
    const btnSubscribe = document.querySelector(".btn-subscribe");

    // 1. 구독하기 버튼 동작
    btnSubscribe.addEventListener("click", (e) => {
      if(!btnSubscribe.classList.contains("active")){
          btnSubscribe.classList.add("active");
      } else{
          btnSubscribe.classList.remove("active");
      }
    })

    // 2. 북마크 버튼 누르면 색깔 바뀌기
    bookmarkButtons.forEach((bookmarkButton) => {
        bookmarkButton.addEventListener("click", (e) => {
            const btnScrap = e.target.parentElement;
            if((e.target === bookmarkButton) && btnScrap.classList.contains("active")){
                btnScrap.classList.remove("active");
            } else if((e.target === bookmarkButton) && !btnScrap.classList.contains("active")){
                btnScrap.classList.add("active");
            }
        })
    })

    // 3. (...)버튼 누르면 추가정보 모달 띄우기
    body.addEventListener("click", (e) => {
        // ... 버튼 누를시 조건문
        if(e.target.classList.contains("fa-ellipsis-h")){
            // dropdown 컨테이너 객체로 저장
            const dropdown = e.target.parentElement.parentElement;
            // dropdown 컨테이너가 켜진 상태가 아닐 때 (모달 꺼져있는 버튼이면)
            if(!dropdown.classList.contains("show")){
                // 모든 dropdown 컨테이너에서 show flag 지우기
                dropdownMenuRights.forEach((dropdownMenuRight) => {
                    dropdownMenuRight.classList.remove("show");
                    dropdownMenuRight.parentElement.classList.remove("show");
                })
                // 누른 ... 버튼의 컨테이너에 show flag 추가
                dropdown.classList.add("show");
                // 모든 dropdown 컨테이너 중 show flag를 가진 컨테이너를 검색
                dropdownMenuRights.forEach((dropdownMenuRight) => {
                    // show flag를 가진 컨테이너의 dropdownMenuRight 컨테이너에 show 추가 (모달 띄우기)
                    if(dropdownMenuRight.parentElement === dropdown && dropdown.classList.contains("show")){
                        dropdownMenuRight.classList.add("show");
                    }
                })
            }
            // dropdown 컨테이너가 켜진 상태 일때 (모달 켜진 버튼 다시 누르면)
            else {
                // 버튼 중에서 해당 컨테이너 검색
                dropdownMenuRights.forEach((dropdownMenuRight) => {
                    if(dropdown.classList.contains("show")){
                        // 해당 컨테이너에서 show flag 삭제 (모달 끄기)
                        dropdownMenuRight.classList.remove("show");
                    }
                })
                // 해당 dropdown 컨테이너에서도 show flag 삭제
                dropdown.classList.remove("show");
            }
        }
        // ... 버튼 아닌 곳 누를시 (모든 모달 끄기: 모든 show flag 삭)
        else {
            dropdownMenuRights.forEach((dropdownMenuRight) => {
                dropdownMenuRight.classList.remove("show");
            })
            ellipsisButtons.forEach((ellipsisButton) => {
                ellipsisButton.parentElement.parentElement.classList.remove("show");
            })
        }
    })


    document.addEventListener("click", (e) => {
        console.log(e.target);
    });
});
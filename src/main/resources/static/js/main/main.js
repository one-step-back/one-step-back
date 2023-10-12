document.addEventListener("DOMContentLoaded", function () {
  const body = document.querySelector("body");
  const blackScreen = document.querySelector(".backdrop");
  const userIcon = document.querySelector(".user-avatar-sm");
  const myMenuProfileAvatar = document.querySelector(".my-menu-profile-avatar");
  const userName = document.querySelector(".user-name");
  const userProfileArrowDown = document.querySelector(
    ".user-profile-arrow-down"
  );
  const userWrapper = document.querySelector(".user-wrapper");
  const profileList = document.querySelector("#profile-list");
  const bookmarkButtons = document.querySelectorAll(".fa-bookmark");
  const ellipsisButtons = document.querySelectorAll(".fa-ellipsis-h");
  const dropdownMenuRights = document.querySelectorAll(".dropdown-menu-right");
  const swiperWrapper = document.querySelector(".swiper-wrapper");
  const swiperSlides = document.querySelectorAll(".swiper-slide");
  const swiperNavigators = document.querySelectorAll(".swiper-navigator");
  const swiperBullets = document.querySelectorAll(".swiper-bullet");

  // 1. 슬라이드 배너
  function autoSlide() {
    swiperWrapper.style.transition = "transform 0.5s";
    count++;
    // 마지막 슬라이드일 때
    // 6번 뒤에 1번 배치시킨다.
    // 6번에서 1번으로 슬라이드 진행
    // 0s를 줘서 원래 1번 위치로 이동(슬라이드 효과는 안보임)
    if (count == 5) {
      swiperBullets[count - 1].classList.remove("active");
      swiperWrapper.style.transform = "translate(-" + 340 * (count + 1) + "px)";
      setTimeout(function () {
        swiperWrapper.style.transition = "transform 0s";
        swiperWrapper.style.transform = "translate(-340px)";
      }, 500);
      count = 0;
      swiperBullets[count].classList.add("active");
    } else {
      swiperBullets[count - 1].classList.remove("active");
      swiperBullets[count].classList.add("active");
      swiperWrapper.style.transform = "translate(-" + 340 * (count + 1) + "px)";
    }
    // 초기화할 버튼객체를 temp에 담는다.
    temp = swiperBullets[count];
  }

  // 배너 번호를 저장할 변수
  let count = 0;
  // 이전에 적용된 버튼 객체를 저장할 변수
  let temp = swiperBullets[count];
  // 첫번째 배너를 복사해서 변수에 담는다.
  const firstSlide = swiperSlides[0].cloneNode(true);
  // 마지막 배너를 복사해서 변수에 담는다.
  const lastSlide = swiperSlides[swiperSlides.length - 1].cloneNode(true);

  // 가장 마지막에 첫번째 배너를 이어 붙인다, 슬라이드 모션이 자연스럽게 1번으로 돌아가게 하기 위함
  swiperWrapper.appendChild(firstSlide);

  // 가장 앞에 마지막 배너를 이어 붙인다, 슬라이드 모션이 자연스럽게 마지막으로 돌아가게 하기 위함
  swiperWrapper.insertBefore(
    lastSlide,
    document.querySelectorAll(".swiper-slide")[0]
  );

  // 첫번째 버튼이 무조건 첫번째 배너이기 때문에 classList에 active 추가
  swiperBullets[count].classList.add("active");

  // 첫번째 배너는 6번이니까 왼쪽으로 한 번 밀어서 1번 보이게 함.
  swiperWrapper.style.transform = "translate(0px)";

  // 4초마다 슬라이드 이동
  let inter = setInterval(autoSlide, 4000);

  // 원하는 번호의 배너 보기
  // 각 버튼마다 클릭 이벤트 적용
  // 버튼을 연속적으로 누르지 못하게 막아주는 FLAG
  let numberButtonCheck = true;
  swiperBullets.forEach((v, i, swiperButton) => {
    // 각 버튼에 click이벤트를 걸어줌.
    swiperButton[i].addEventListener("click", function () {
      // 아래의 얍삽한 방법으로 인해 0s로 변할 수 있기 때문에 무조건 0.5s로 설정하고 시작
      swiperWrapper.style.transition = "transform 0.5s";
      if (numberButtonCheck) {
        // 0.5초가 지나고 나서 클릭했거나 처음 클릭하거나
        numberButtonCheck = false; // 누르자마자 바로 false
        clearInterval(inter); // autoSlide 타이머 제거, 동시에 돌아가면 안됨.
        count = i; // 클릭한 버튼의 인덱스를 배너의 번호로 설정
        temp.classList.remove("active"); // 이전에 적용된 버튼 초기화.
        swiperBullets[count].classList.add("active"); // 클릭한 버튼을 active로 설정.
        swiperWrapper.style.transform =
          "translate(-" + 340 * (count + 1) + "px)"; // 클릭한 버튼의 인덱스번호를 통해 배너번호로 이동
        temp = swiperBullets[count]; // 지금 선택된 버튼 객체 담아주기
        inter = setInterval(autoSlide, 4000); //버튼 클릭 다했으니까 auto slide 다시 작동
        // 클릭하고 나서 할 거 다 하고 0.5초 후에 FLAG를 true로 변경
        // 0.5초 안에는 다시 클릭 못하게 막아주기
        setTimeout(function () {
          numberButtonCheck = true;
        }, 500);
      }
    });
  });

  // 이전 버튼, 다음 버튼 구현
  let arrowButtonCheck = true;
  swiperNavigators.forEach((swiperNavigatorButton) => {
    swiperNavigatorButton.addEventListener("click", function () {
      if (arrowButtonCheck) {
        arrowButtonCheck = false;
        clearInterval(inter);
        swiperWrapper.style.transition = "transform 0.5s";
        if (swiperNavigatorButton.classList.contains("fa-chevron-left")) {
          count--;
          if (count == -1) {
            swiperWrapper.style.transform = "translate(0px)";
            setTimeout(function () {
              swiperWrapper.style.transition = "transform 0s";
              swiperWrapper.style.transform = "translate(-1700px)";
            }, 500);
            count = 4;
          } else {
            swiperWrapper.style.transform =
              "translate(-" + 340 * (count + 1) + "px)";
          }
        } else {
          count++;
          if (count == 5) {
            swiperWrapper.style.transform =
              "translate(-" + 340 * (count + 1) + "px)";
            setTimeout(function () {
              swiperWrapper.style.transition = "transform 0s";
              swiperWrapper.style.transform = "translate(-340px)";
            }, 500);
            count = 0;
          } else {
            swiperWrapper.style.transform =
              "translate(-" + 340 * (count + 1) + "px)";
          }
        }
        temp.classList.remove("active");
        temp = swiperBullets[count];
        swiperBullets[count].classList.add("active");
        inter = setInterval(autoSlide, 4000);
        setTimeout(function () {
          arrowButtonCheck = true;
        }, 500);
      }
    });
  });

  // 2. 프로필 누르면 모달창 띄우기
  userIcon.addEventListener("click", (e) => {
    if (!body.classList.contains("user-menu-show")) {
      body.classList.add("user-menu-show");
      console.log(body.classList);
      blackScreen.style.display = "block";
    }
  });

  // 3. 프로필 켜진 상태에서 다른 화면 누르면 모달 끄기
  blackScreen.addEventListener("click", (e) => {
    body.classList.remove("user-menu-show");
    console.log(body.classList);
    blackScreen.style.display = "none";
  });

  // 4. 유저 모달에서 프로필 컨테이너 클릭하면 프로필 리스트 띄우기 / 내리기
  function changeShow() {
    console.log("유저 래퍼 초기값: " + userWrapper.classList);
    if (!userWrapper.classList.contains("show")) {
      userWrapper.classList.add("show");
      profileList.classList.add("show");
      console.log("유저 래퍼에 show 추가: " + userWrapper.classList);
    } else {
      userWrapper.classList.remove("show");
      profileList.classList.remove("show");
      console.log("유저 래퍼 show 지우기: " + userWrapper.classList);
    }
  }
  myMenuProfileAvatar.addEventListener("click", changeShow);
  userName.addEventListener("click", changeShow);
  userProfileArrowDown.addEventListener("click", changeShow);

  // 5. 북마크 버튼 누르면 색깔 바뀌기
  bookmarkButtons.forEach((bookmarkButton) => {
    bookmarkButton.addEventListener("click", (e) => {
      const btnScrap = e.target.parentElement;
      if (
        e.target === bookmarkButton &&
        btnScrap.classList.contains("active")
      ) {
        btnScrap.classList.remove("active");
      } else if (
        e.target === bookmarkButton &&
        !btnScrap.classList.contains("active")
      ) {
        btnScrap.classList.add("active");
      }
    });
  });

  // 6. (...)버튼 누르면 추가정보 모달 띄우기
  body.addEventListener("click", (e) => {
    // ... 버튼 누를시 조건문
    if (e.target.classList.contains("fa-ellipsis-h")) {
      // dropdown 컨테이너 객체로 저장
      const dropdown = e.target.parentElement.parentElement;
      // dropdown 컨테이너가 켜진 상태가 아닐 때 (모달 꺼져있는 버튼이면)
      if (!dropdown.classList.contains("show")) {
        // 모든 dropdown 컨테이너에서 show flag 지우기
        dropdownMenuRights.forEach((dropdownMenuRight) => {
          dropdownMenuRight.classList.remove("show");
          dropdownMenuRight.parentElement.classList.remove("show");
        });
        // 누른 ... 버튼의 컨테이너에 show flag 추가
        dropdown.classList.add("show");
        // 모든 dropdown 컨테이너 중 show flag를 가진 컨테이너를 검색
        dropdownMenuRights.forEach((dropdownMenuRight) => {
          // show flag를 가진 컨테이너의 dropdownMenuRight 컨테이너에 show 추가 (모달 띄우기)
          if (
            dropdownMenuRight.parentElement === dropdown &&
            dropdown.classList.contains("show")
          ) {
            dropdownMenuRight.classList.add("show");
          }
        });
      }
      // dropdown 컨테이너가 켜진 상태 일때 (모달 켜진 버튼 다시 누르면)
      else {
        // 버튼 중에서 해당 컨테이너 검색
        dropdownMenuRights.forEach((dropdownMenuRight) => {
          if (dropdown.classList.contains("show")) {
            // 해당 컨테이너에서 show flag 삭제 (모달 끄기)
            dropdownMenuRight.classList.remove("show");
          }
        });
        // 해당 dropdown 컨테이너에서도 show flag 삭제
        dropdown.classList.remove("show");
      }
    }
    // ... 버튼 아닌 곳 누를시 (모든 모달 끄기: 모든 show flag 삭)
    else {
      dropdownMenuRights.forEach((dropdownMenuRight) => {
        dropdownMenuRight.classList.remove("show");
      });
      ellipsisButtons.forEach((ellipsisButton) => {
        ellipsisButton.parentElement.parentElement.classList.remove("show");
      });
    }
  });

  document.addEventListener("click", (e) => {
    console.log(e.target);
  });
});

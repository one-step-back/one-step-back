
// join
const $joinInputs = $("div.join input[type='text'], div.join input[type='password']");
const nameRegex = /^[가-힣|a-z|A-Z|]+$/;
const specialCharacterRegex = /[`~!@#$%^&*()_|+\-=?;:'",.<>\{\}\[\]\\\/]/gim;
const passwordNumberRegex =/[0-9]/g;
const passwordEnglishRegex = /[a-z]/ig;
const passwordSpecialCharacterRegex = /[`~!@@#$%^&*|₩₩₩'₩";:₩/?]/gi;
const emailFirstRegex =  /[`~!@#$%^&*|\\\'\";:\/?]/;
const emailLastRegex = /[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;
let joinBlurMessages = ["이메일을 입력하세요.", "이메일 도메인을 입력하세요.", "비밀번호를 입력하세요.", "비밀번호 확인을 위해 한번 더 입력하세요.", "닉네임을 입력하세요."];
let joinRegexMessages = ["이메일 주소를 확인해주세요.", "이메일 도메인을 확인해주세요.", "공백 제외 영어 및 숫자, 특수문자 모두 포함하여 10~20자로 입력해주세요.", "위 비밀번호와 일치하지 않습니다. 다시 입력해주세요.", "영문 혹은 한글로 2자~20자로 입력해주세요."];
const $joinHelp = $("div.join p.help");
let joinCheck;
let joinCheckAll = [false, false, false, false, false];

$joinInputs.on("blur", function(){
    let i = $joinInputs.index($(this));
    let value = $(this).val();

    $(this).next().hide();
    $(this).next().fadeIn(500);

    if(!value){
        $joinHelp.eq(i).text(joinBlurMessages[i]);
        showHelp($(this), "error.png");
        joinCheck = false;
        joinCheckAll[i] = joinCheck;
        return;
    }

    switch(i){
        case 0: // 이메일
            joinCheck = !emailFirstRegex.test(value);
            break;
        case 1: // 도메인
            var condition1 = emailLastRegex.test(value);
            var condition2 = value.substring(value.indexOf(".") + 1).length > 1;
            joinCheck = condition1 && condition2;
            break;
        case 2: // 비밀번호
            let numberCheck = value.search(passwordNumberRegex);
            let englishCheck = value.search(passwordEnglishRegex);
            let specialCharacterCheck = value.search(passwordSpecialCharacterRegex);

            var condition1 = (numberCheck >= 0 && englishCheck >= 0) && (englishCheck >= 0 && specialCharacterCheck >= 0) && (specialCharacterCheck >= 0 && numberCheck >= 0)
            var condition2 = value.length > 10 && value.length < 21;
            var condition3 = value.search(/\s/) < 0;

            joinCheck = condition1 && condition2 && condition3;
            break;
        case 3: // 비밀번호 확인
            joinCheck = $joinInputs.eq(i-1).val() == value;
            break;
        case 4: // 닉네임
            joinCheck = value.length > 1 && value.length < 21 && nameRegex.test(value) && !specialCharacterRegex.test(value);
            break;
    }

    joinCheckAll[i] = joinCheck;

    if(!joinCheck){
        $joinHelp.eq(i).text(joinRegexMessages[i]);
        $joinHelp.eq(i).css('color', 'red')
        showHelp($(this), "error.png");
        return;
    }

    if(i != 0) {
        $joinHelp.eq(i).text("");
        showHelp($(this), "pass.png");
    }
});

$("select.email").on("change", function(){
    $("div.email-last input").val($(this).val());
    $joinInputs.eq(1).trigger("blur");
    if(!$(this).val()){
        $("div.email-last input").prop("readonly", false);
        return;
    }
    $("div.email-last input").prop("readonly", true);
});

function showHelp($input, fileName){
    $input.next().attr("src", "/images/" + fileName);

    if(fileName == "pass.png") {
        $input.css("border", "1px solid rgba(0, 0, 0, 0.1)");
        $input.css("background", "rgb(255, 255, 255)");
        $input.next().attr("width", "18px");
    }else {
        $input.css("border", "1px solid rgb(255, 64, 62)");
        $input.css("background", "rgb(255, 246, 246)");
    }
}

function checkEmail() {
    //    아이디 중복검사
    $.ajax({
        url: `/member/check-email/${$("div.email-first input").val() + '@' + $("div.email-last input").val()}`,
        success: function(result){
            if(result){
                $joinHelp.eq(0).text("사용중인 이메일입니다.");
                $joinHelp.eq(0).css('color', 'red')
                joinCheckAll[0] = false;
                joinCheckAll[1] = false;
                showHelp($joinInputs.eq(0), "error.png");
            } else{
                $joinHelp.eq(0).text("사용 가능한 이메일이에요!");
                $joinHelp.eq(0).css('color', '#7eb347')
                joinCheckAll[0] = true;
                joinCheckAll[1] = true;
                showHelp($joinInputs.eq(0), "pass.png");
            }
        }
    });
}

function send(){
    $joinInputs.eq(2).trigger("blur");
    $joinInputs.eq(3).trigger("blur");
    $joinInputs.eq(4).trigger("blur");
    if(joinCheckAll.filter(check => check).length != $joinInputs.length){
        let modalMessage = "<span>모든 정보를 정확히 입력하셔야</span><span>가입이 완료됩니다.</span>";
        showWarnModal(modalMessage);
        return;
    }

    /*비밀번호 암호화(인코딩)*/
    $("input[name='memberPassword']").val(btoa($("input[name='memberPassword']").val()));
    $("#password-check").val(btoa($("#password-check").val()));

    /*이메일 합치기*/
    $("input[name='memberEmail']").val($("div.email-first input").val() + '@' + $("div.email-last input").val())

    document.join.submit();
}
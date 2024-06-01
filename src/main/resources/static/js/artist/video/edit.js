const $tagWrap = $("#tag-wrap");
const $tagInputs = $("#tagInputs");
const $modal = $("#modal");
const $postForm = $("#post-form");
const $recheckForm = $("#recheck-form");

let isSubmitted = false;

$("#tag-input").on("keydown", function (e) {
    if (e.key === "Enter") {
        let tagValue = $(this).val().trim();

        if (tagValue !== "") {
            if (tagCount >= 5) {
                alert("태그는 최대 5개까지 작성 가능합니다.");
                return;
            }

            tagValue = tagValue.replace(/ /g, "_");

            let isDuplicate = false;
            $tagWrap.find(".tag-container").each(function () {
                if ($(this).text() === tagValue) {
                    isDuplicate = true;
                    return false;
                }
            });

            if (isDuplicate) {
                alert("이미 추가된 태그입니다.");
                return;
            }

            tagCount++;

            let tagContainer = `
                    <div class="tag-container">
                        <span>${tagValue}</span>
                        <i class="fa-solid fa-x"></i>
                    </div>
                `;
            let tagInput = `
                    <input name="tags" value="${tagValue}" type="hidden">
                `;

            $tagWrap.append(tagContainer);
            $tagInputs.append(tagInput);

            $(this).val("");
        }
    }
});

$tagWrap.on("click", ".tag-container i", function () {
    const deletedTag = $(this).parent().text().trim();
    $(this).parent().remove();
    tagCount--;

    $tagInputs.find("input").each(function () {
        if ($(this).val() === deletedTag) {
            $(this).remove();
        }
    });
});

$("#popover-on-btn").click(() => {
    let inputs = {
        "#title": "제목",
        "#videoLink": "유튜브링크",
        "#content": "내용",
        "#category": "카테고리"
    };

    for (let selector in inputs) {
        let input = $(selector);
        if (input.val() === undefined || input.val() === "") {
            let fieldName = inputs[selector];
            alert(fieldName + "을(를) 입력해 주세요.");
            return;
        }
    }

    $modal.css("display", "block");
    $postForm.css("display", "block")
});

$(".modal-cancel").click(() => {
    $modal.hide();
    $postForm.hide();
    $recheckForm.hide();
})

function doubleSubmitBlocker() {
    if (isSubmitted) {
        return isSubmitted;
    } else {
        isSubmitted = true;
        return false;
    }
}

$("#summit-btn").click(() => {
    if (doubleSubmitBlocker()) return;
    $("#summit-form").submit();
});

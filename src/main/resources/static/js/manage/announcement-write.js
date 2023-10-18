$(function() {
    const modal = $("#modal");

    $("#summit-btn").click(() => {
        modal.css("display", "block");
    });

    $("#modal-cancel").click(()=>{
        modal.css("display", "none")
    })

    $("#tag-input").on("input", function() {
        const inputValue = $(this).val();
        const sanitizedValue = sanitizeInput(inputValue);
        $(this).val(sanitizedValue);
    });

    $("#tag-input").on("keydown", function (event) {
        if (event.key === "Enter") {
            let tagValue = $(this).val().trim();
            const tagWrap = $("#tag-wrap");

            if (tagValue !== "") {
                const tagCount = tagWrap.find(".tag-container").length;
                if (tagCount >= 5) {
                    alert("태그는 최대 5개까지 작성 가능합니다.");
                    return;
                }

                // 공백(space)을 밑줄(_)로 대체
                tagValue = tagValue.replace(/ /g, "_");

                let tagContainer = `<div class="tag-container">${tagValue}<i class="fa-solid fa-x"></i></div>`;

                tagWrap.append(tagContainer);

                $(this).val("");
            }
        }
    });

    $("#tag-wrap").on("click", ".tag-container i", function() {
        $(this).parent().remove();
    });
});

function sanitizeInput(input) {
    return input.replace(/[ \{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\\(\=]/g, "");
}
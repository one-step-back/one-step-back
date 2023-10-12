$(function () {
    $("input[type='file']").one("change", function (event) {
        const selectedFile = event.target.files[0];
        if (selectedFile) {
            const reader = new FileReader();

            reader.onload = function (e) {
                const image = document.createElement("img");
                image.src = e.target.result;

                $(".profile-avatar").html(image);
            };

            reader.readAsDataURL(selectedFile);
        }
    });
});
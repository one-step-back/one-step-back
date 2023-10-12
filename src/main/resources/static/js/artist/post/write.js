document.addEventListener("DOMContentLoaded", function () {
    const insertFile = document.querySelector("#insert-file");
    const urlForm = document.querySelector("#embed-url");

    insertFile.addEventListener("click", (e) => {
        if(urlForm.style.display === "none"){
            urlForm.style.display = "block";
        } else{
            urlForm.style.display = "none"
        }
    })
});
$(function (){
    $(".h-user-avatar").click(function (){
        $(".h-user-menu-popover").toggleClass("active");
    })
})



const postImages = $('#main').find(".photoset")
postImages.on('auxclick', function(e) {
    e.preventDefault();
})
postImages.on('click', function(e) {
    if (e.button === 1) {
        e.preventDefault();
    }
})


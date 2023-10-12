$(function (){
    $(".h-user-avatar").click(function (){
        $(".h-user-menu-popover").toggleClass("active");
    })

    $("button.h-nav-item").click(function (){
        $(".h-notification-popover-container").toggleClass("active");
    })
})
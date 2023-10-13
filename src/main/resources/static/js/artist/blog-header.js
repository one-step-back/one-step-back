$(function (){
    $(".h-user-avatar").click(function (){
        $(".h-user-menu-popover").toggleClass("active");
    })

    $(".h-nav-item.bell").click(function (){
        $(".h-notification-popover-container").toggleClass("active");
    })
})
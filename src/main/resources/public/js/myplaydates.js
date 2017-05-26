$(document).ready(function () {

    $('.remove-playdate-link').click(function (e) {
        e.preventDefault();
        var playdateid = $(this).data('playdateid');
        var div = $(this).closest('.removeable-playdate-parent');
        console.log("trying to delete playdate with id = " + playdateid);
        $.ajax('/protected/deleteplaydate?playdateId=' + playdateid, {
            type: 'DELETE',
            success: function(res){
                window.location.replace("/protected/showplaydates");
                console.log("success remove playdate");
                console.log(res)
                div.remove();
            },
            error: function(res){
                console.log("failed to remove playdate")
                console.log(res)
            }
        })

    });


    var swipable = $('.swipable-remove-playdate');
    $.each(swipable, function (index, s) {
        var hammer = new Hammer(swipable[index]);
        hammer.on('swiperight', function(e){
            console.log('swipe');
            $(swipable[index]).animate({
                left: '+=100',
                opacity: 0.25
            }, 750, function () {
                $.ajax({
                    type: 'DELETE',
                    url: '/protected/deleteplaydate?playdateId=' + $(s).data('playdateid'),
                    success: function(res) {
                        console.log(res);
                        $(swipable[index]).remove();
                    },
                    error: function(res){
                        console.log('error' + res)
                    }
                });
            });
        })
    });


});
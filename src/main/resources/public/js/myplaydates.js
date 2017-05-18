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

});
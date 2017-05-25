$(document).ready(function () {

    $('.remove-participant-link').click(function(e){
        e.preventDefault();
        console.log("inside link");
        var parent = $(this).closest('li');
        $.ajax({
            type: 'DELETE',
            url: $(this).attr('href'),
            success: function(res){
                console.log("success remove participant");
                parent.remove();
            },
            error: function(res){
                console.log(res);
                console.log("error remove participant");
            }
        })


    });




});

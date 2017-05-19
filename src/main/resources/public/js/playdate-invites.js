$(document).ready(function () {

    $('#invite-friends-list').on('click','.add-friend-to-playdate',function(e) {
        e.preventDefault();

        var li = $(this).closest('li')
        $.ajax({
            type: 'POST',
            url: '/protected/sendivite',
            data: {
                userId: $(this).data('userid'),
                playdateId: $(this).data('playdateid')
            },
            success: function(res) {
                li.remove();
            },
            error: function(res){
                console.log(res);
            }
        })

    })

});
$(document).ready(function () {

    $('#add-friend').click(function (e) {
        e.preventDefault();

        $.post('/protected/sendfriendrequest?friendId=' + $(this).data('userid'), function (res) {
            reloadPage();
        })

    });

    $('#remove-friend').click(function (e) {
        e.preventDefault();
        $.ajax({
            type: 'DELETE',
            url: '/protected/removefriend?userId=' + $(this).data('userid'),
            success: function (res) {
                reloadPage();
            }
        });
    });

    function reloadPage() {
        location.reload(true);
    }

    $('#remove-friendrequest').click(function (e) {
        e.preventDefault();
        $.ajax({
            type: 'DELETE',
            url: '/protected/removefriendshiprequest?userId=' + $(this).data('userid'),
            success: function (res) {
                reloadPage();
            }
        });
    });
    $('#accept-friendrequest').click(function (e) {
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: '/protected/acceptfriendshiprequest?userId=' + $(this).data('userid'),
            success: function (res) {
                reloadPage();
            }
        });
    });
    $('#decline-friendrequest').click(function (e) {
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: '/protected/declinefriendshiprequest?userId=' + $(this).data('userid'),
            success: function (res) {
                reloadPage();
            }
        });
    });


    $('#send-report').click(function (e) {
        e.preventDefault();
        e.stopImmediatePropagation();

        $.ajax({
            type: 'POST',
            url: '/protected/postreport',
            data: $('#makeReport').serialize(),
            success: function (res) {
                console.log(res);
                $('#report').val("");
                $('#popupReport').popup('close');

            },
            error: function (res) {
                console.log(res);
                $('#report-validation-message').show().text("Rapporten måste vara mellan 10 och 300 tecken lång");
            }
        });
    })
});
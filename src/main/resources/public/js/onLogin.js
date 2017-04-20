$(document).ready(function () {

    var username_field = $('#username');
    var password_field = $('#password');

    $('#loginform').submit(function (e) {
        e.preventDefault();
        // console.log("username = " + username_field.val());
        // console.log("password = " + password_field.val());

        var data = {"username": username_field.val(), "password": password_field.val()};
        console.log("json = " + data);
        console.log("json = " + data.username);
        console.log("json = " + data.password);

        $.post('/trylogin', data, function (result) {
            alert(result);
        });

        /*$.ajax({
            url: '/trylogin',
            type: "POST",
            data: data,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (result) {
                alert(result);
            }

        });*/

    });




});
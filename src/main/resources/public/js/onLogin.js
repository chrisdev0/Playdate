$(document).ready(function () {

    var username_field = $('#username_field');
    var password_field = $('#password_field');

    $('#loginform').submit(function (e) {
        e.preventDefault();
        console.log("username = " + $('#username_field').val());
        console.log("password = " + $('#password_field').val());

        var data = {username: username_field.val(), password: password_field.val()};


        $.ajax({
            url: '/trylogin',
            type: "POST",
            data: data,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (result) {
                console.log("Result of login = " + result);
            }

        });

    });




});
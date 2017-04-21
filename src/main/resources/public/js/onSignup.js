$(document).ready(function () {


    var email_field = $('#username');
    var name_field = $('#name');
    var password_field = $('#password');
    var password_field_validation = $('#password2');

    $('#signform').submit(function (e) {
        e.preventDefault();

        if(!validatePassword(password_field,password_field_validation)){
            showInfoErrorDialog();
            return;
        }


        var data = {"username": email_field.val(), "password": password_field.val()};
        console.log("json = " + data);
        console.log("json = " + data.username);
        console.log("json = " + data.password);

        $.post('/trysignup', data, function (result) {
            alert(result);
        });


    });

    function showInfoErrorDialog() {

    }

    function checkPasswordIsEqual(pass1, pass2) {
        return pass1 !== undefined && pass2 !== undefined && (pass1 === pass2)
    }

    function validatePassword(pass1, pass2) {
        if(!checkPasswordIsEqual(pass1,pass2)) {
            return false;
        }
        if(pass1.length < 6) {
            return false;
        }
        if(pass1.length > 255) {
            return false;
        }
        return regexEmailValidation(pass1);
    }

    function regexPasswordValidation(password) {
        //todo
        return true;
    }

    function regexEmailValidation(email) {
        //todo
        return true;
    }








});
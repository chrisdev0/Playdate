$(document).ready(function () {


    var divOutput = $('#potential-friends-output');

    $('#friends-searchterm').on('input', function () {
        console.log("input");
        var text = $(this).val();
        if (text.length > 2) {

            $.getJSON('/protected/getpotentialfriends?searchTerm=' + text, function (res) {
                console.log(res);
                renderFriends(res);
            });
        }
    });


    var renderFriends = function(friends) {
        divOutput.html("");
        var ooutput = "";
        $.each(friends, function (index, friend) {
            var output = '<li>' +
                "<div class='potential-friends-list' data-user-id='user.id' >' + friend.name + '</div>" +
                '</li>';
            ooutput += output;
        });
        divOutput.html(ooutput);
    };


});

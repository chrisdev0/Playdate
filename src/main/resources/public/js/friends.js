$(document).ready(function () {


    var divOutput = $('#potential-friends-output');

    $('#friends-searchterm').on('input', function () {
        var text = $(this).val();
        if (text.length > 2) {
            $.getJSON('/protected/getpotentialfriends?searchTerm=' + text, function (res) {
                renderFriends(res);
            });
        }
    });


    var renderFriends = function(friends) {
        divOutput.html("");
        var ooutput = "";
        $.each(friends, function (index, friend) {
            ooutput += '<div>' +
                '<p>' + friend.name + '</p>' +
                '</div>';
        });
        divOutput.html(ooutput);
    };



});

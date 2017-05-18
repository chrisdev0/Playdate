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
            var output = '<div>' +
                '<p>' + friend.name + '</p>' +
                '</div>';
            ooutput += output;
        });
        divOutput.html(ooutput);
    };


});

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
        $.each(friends, function (index, user) {
            ooutput += '<li>' +
                '<div class="potential-friends-item" >' +
                    '<div class="composite-friend-item">' +
                        '<img class="small-profile" src="' + user.profilePictureUrl + '">' +
                        '<span><a data-ajax="false" href="/protected/showuser?userId='+ user.id +'" ' + user.name +'</span>' +
                        '<a data-ajax="false" class="add-user-as-friend" data-user-id=' + user.id + '></a> ' +
                    '</div>' +
                '</div>' +
                '</li>';
        });
        divOutput.html(ooutput);
    };


});

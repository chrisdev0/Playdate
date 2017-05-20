$(document).ready(function () {

    var invitelist = $('#invite-friends-list');

    invitelist.on('click', '.add-friend-to-playdate', function (e) {
        e.preventDefault();

        var li = $(this).closest('li')
        $.ajax({
            type: 'POST',
            url: '/protected/sendivite',
            data: {
                userId: $(this).data('userid'),
                playdateId: window.playdateid
            },
            success: function (res) {
                li.remove();
            },
            error: function (res) {
                console.log(res);
            }
        })
    });

    function renderFriendsToInvite(friends) {
        var ooutput = "";
        invitelist.html("");
        $.each(friends, function (index, user) {
            ooutput += '<li>' +
                '<p>' +
                '<img src="' + user.profilePictureUrl + '" class="img-size">' +
                user.name +
                '<a href="#" class="add-friend-to-playdate" data-ajax="false" data-userid="' + user.id + '">Bjud in vän</a>' +
                '</p>' +
                '</li>';
        });

        invitelist.html(ooutput);
        invitelist.listview('refresh');

    }

    var getPotentialFriendsToInvite = function() {
        $.getJSON('/protected/getpotentialfriendstoinvite?playdateId=' + window.playdateid, function (res) {
            if(res.length === 0){
                renderNoFriendsToInvite();
            } else {
                renderFriendsToInvite(res)
            }

        });
    };

    getPotentialFriendsToInvite();

    var renderNoFriendsToInvite = function() {
        invitelist.html("<li class='no-friends-to-invite'>Du har inga vänner att bjuda in</li>");
        invitelist.listview('refresh');
    };

});
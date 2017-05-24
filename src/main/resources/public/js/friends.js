$(document).ready(function () {

    var friendrequests = $('.swipable-friend-request');

    $.each(friendrequests, function (index, f) {
        var hammertime = new Hammer(friendrequests[index]);
        hammertime.on('swiperight', function (e) {
            console.log("swipe");
            var target = $(e.target).closest('li');
            target.animate({
                left: '+=100',
                opacity: 0.25
            },750,function() {
                $.ajax({
                    type: 'POST',
                    url: '/protected/declinefriendshiprequest?userId=' + $(target).find('.friendship-request-popup').data('userid'),
                    success: function(res){
                        target.remove();
                    },
                    error: function(res) {
                        console.log("error removing friendrequest")
                    }
                });

            })
        });
    });

    var potentialfriendsouput = $('#potential-friends-output');

    $('#friends-searchterm').on('input', function () {
        var text = $(this).val();
        if (text.length > 2) {
            $.getJSON('/protected/getpotentialfriends?searchTerm=' + text, function (res) {
                renderFriends(res, potentialfriendsouput,'add-friend-btn');
            });
        }
    });


    var renderFriends = function(friends, output, btnclass) {
        output.html("");
        var ooutput = "";
        $.each(friends, function (index, user) {
            ooutput += '<li>' +
                '<a data-ajax="false" href="/protected/showuser?userId=' + user.id +'"><img src="'+ user.profilePictureUrl +'" class="img-size">' + user.name + '</a>' +
                '<a href="#" class="'+btnclass+'" data-userid="'+ user.id +'" data-ajax="false"></a>' +
                '</li>';
        });
        output.html(ooutput);
        output.listview();
        output.listview('refresh');
    };





    potentialfriendsouput.on('click', '.add-friend-btn', function (e) {
        e.preventDefault();
        var li = $(this).closest('li');
        $.ajax({
            type: 'POST',
            url: '/protected/sendfriendrequest?friendId=' + $(this).data('userid'),
            success: function (res) {
                li.remove();
                runGetSentRequest()
                $('#resPopup p').text('Vänförfrågan skickad');
                $('#resPopup').popup('open');
            },
            error: function (res) {
                $('#resPopup p').text('Något gick fel, prova igen senare');
                $('#resPopup').popup('open');
            }
        });
    });


    var sentRequestOutput = $('#sent-request-output');

    var runGetSentRequest = function () {
        $.getJSON('/protected/getsentfriendshiprequest',function(friends) {
            renderFriends(friends, sentRequestOutput, 'remove-friend-request');
        })
    };

    runGetSentRequest();


    sentRequestOutput.on('click', '.remove-friend-request', function (e) {
        e.preventDefault();
        var li = $(this).closest('li');
        $.ajax({
            type: 'DELETE',
            url: '/protected/removefriendshiprequest?userId=' + $(this).data('userid'),
            success: function(res) {
                li.remove();
            },
            error: function(res){
                console.log("error removing friends")
            }
        });

    });





});

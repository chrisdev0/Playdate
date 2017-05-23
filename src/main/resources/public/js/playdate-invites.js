$(document).ready(function () {

    var invitelist = $('#invite-friends-list');

    $.getJSON('/protected/getcommentsofplaydate?playdateId=' + window.playdateid, function (res) {
        console.log(res);
        $('#show-comments').html("");
        $.each(res, function (index, comment) {
            renderComment(comment)
        });
    });

    var renderComment = function (comment) {
        var output = "<div class='bottom-line'>";
        var date = new Date(comment.commentDate);

        output += "<p style='font-weight: bold;'><a class='user-name-comment' href='/protected/showuser?userId=" + comment.commenter.id + "'>" + comment.commenter.name + "</a>" +
            " den " + date.getDate() + "/" + date.getMonth() + " " + date.getFullYear() + " klockan " +
            date.getHours() + ":" + (date.getMinutes() < 10 ? "0" : "") + date.getMinutes() + "</p>";
        output += "<p>" + comment.comment + "</p>"
        output += "</div>"
        $('#show-comments').append(output);
    };

    $('#makeComment').submit(function(e) {
        e.preventDefault();
        e.stopImmediatePropagation();

        $.ajax({
            type: 'POST',
            url: '/protected/postplaydatecomment',
            data: $('#makeComment').serialize(),
            success: function(res) {
                console.log(res);
                res = JSON.parse(res);
                $('#comment').val("");
                $('#show-comments').html("");
                $.each(res,function(index, comment) {
                    renderComment(comment)
                })
            }
        })
    });

    $("#post-comment").one(function(e){
        e.preventDefault();
        $("#comment-form").submit();
        $("#popupBasic").popup('close');
    });

    $('.remove-playdate-link').click(function (e) {
        e.preventDefault();
        var playdateid = $(this).data('playdateid');
        var div = $(this).closest('.removeable-playdate-parent');
        console.log("trying to delete playdate with id = " + playdateid);
        $("#popupYes").attr('href','/protected/deleteplaydate?playdateId=' + playdateid)
        $("#popupValidate").popup("open");

    });
    $('#popupYes').click(function (e) {
        console.log("Yes clicked")
        e.preventDefault()
        $.ajax($(this).attr('href'), {
            type: 'DELETE',
            success: function(res){
                window.location.replace("/protected/showplaydates");
                console.log("success remove playdate");
                console.log(res)

            },
            error: function(res){
                console.log("failed to remove playdate")
                console.log(res)
                alert("Kunde inte ta bort Playdate!")
            }
        });
    });



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
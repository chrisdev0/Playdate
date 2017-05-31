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

        var userCanDelete = false;
        if (window.userId == comment.commenter.id) {
            userCanDelete = true;
        }
        output += "<p style='font-weight: bold;'><a class='user-name-comment' href='/protected/showuser?userId=" + comment.commenter.id + "'>" + comment.commenter.name + "</a>" +
             " " + date.getDate() + "/" + date.getMonth() + " " + date.getFullYear() + " " +
            date.getHours() + ":" + (date.getMinutes() < 10 ? "0" : "") + date.getMinutes() + "</p>";
        output += "<p>" + comment.comment;

        if (userCanDelete) {
            output += '<a  style="display: block; font-size: 1em" href="/protected/removecomment?commentId=' + comment.id + '" class="remove-comment-link">Ta bort</a>';
        }

        output += "</p>"
        output += "</div>"
        $('#show-comments').append(output);
    };

    $('#show-comments').on('click', '.remove-comment-link',function(e) {
        e.preventDefault();
        var commenDiv = $(this).closest('.bottom-line');
        $.ajax({
            type: 'DELETE',
            url: $(this).attr('href'),
            success: function(res) {
                commenDiv.remove();
            },
            error: function(res) {
                alert("Fel, kommentaren gick inte att ta bort")
            }
        })

    })

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
            },
            error: function (res) {
                $('#validationPopup p').html("Kommentaren måste vara mellan 6 och 300 tecken lång");
                $('#validationPopup').popup('open');
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
    $('#popupYes').one('click',function (e) {
        e.preventDefault();
        $.ajax($(this).attr('href'), {
            type: 'DELETE',
            success: function(res){
                window.location.replace("/protected/showplaydates");
            },
            error: function(res){
                console.log("failed to remove playdate")
                alert("Kunde inte ta bort Playdate!")
            }
        });
    });



    invitelist.on('click', '.add-friend-to-playdate', function (e) {
        e.preventDefault();

        var li = $(this).closest('li');
        li.hide();
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
                li.show();
            }
        })
    });

    function renderFriendsToInvite(friends) {
        var ooutput = "";
        invitelist.html("");
        $.each(friends, function (index, user) {
            ooutput += '<li>' +
                '<a data-ajax="false" href="/protected/showuser?userId='+ user.id +'">' +
                '<img src="' + user.profilePictureUrl + '" class="img-size">' + user.name +
                '</a><a href="#" class="add-friend-to-playdate" data-ajax="false" data-userid="' + user.id + '">Bjud in vän</a>' +
                '' +
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
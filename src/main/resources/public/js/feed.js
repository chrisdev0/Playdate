$(document).ready(function () {


    var outputDiv = $('#output');
    $.mobile.loading('show',{ theme:"b"}).css("box-shadow", "none")
        .css("border", "none").css("outline", "none")

    navigator.geolocation.getCurrentPosition(function (loc) {

        console.log(loc)
        $.mobile.loading('hide')
        $.getJSON('/protected/getfeed?locX=' + loc.coords.latitude + '&locY=' + loc.coords.longitude, function (res) {
            console.log(res);
            $.each(res, function (index, feedObject) {
                renderFeedObject(feedObject);
            })
        });
    }, function () {
        outputDiv.html("");
        outputDiv.append("<p>Var god sätt på platstjänster!</p> " +
            "<a class='ui-btn' href='#' onclick='window.location.reload(true)'>Ladda om sidan</a>");
    });

    var renderFeedObject = function (feed) {
        var feedObjectType = feed.objectTypeId;
        var outputStr = '<div class="feed-content">' +
            '<div class="feed-image-composite">' +
            '<div class="feed-object-type object-type-'+ feedObjectType+'"><span>' + feed.category + '</span></div>' +
            '<a data-ajax="false" href="' + feed.onClickUrl +'"><img src="/protected/'+ feed.imageUrl+'">'+
            '<h1>'+feed.header+'</h1>' +
            '</a></div>' +
            '<p class="feed-object-description">'+ feed.shortDescription;

        if(feedObjectType == 0 || feedObjectType == 4) {
            outputStr += '<a data-ajax="false" href="'+ feed.onClickUrl +'"><button type="ui-btn ui-btn-inline ui-corner-all" class="ui-btn ui-btn-inline ui-corner-all">Visa Playdate</button></a>';



        } else if (feedObjectType == 1) {

            outputStr += '<a data-ajax="false" href="'+ feed.onClickUrl +'"> <button type="ui-btn ui-btn-inline ui-corner-all" class="ui-btn ui-btn-inline ui-corner-all">Visa Plats</button></a>';

        }

        outputStr+= '</p></div>';
        outputDiv.append(outputStr);
    };

});
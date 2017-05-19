$(document).ready(function () {


    var outputDiv = $('#output');
    navigator.geolocation.getCurrentPosition(function (loc) {
        console.log(loc)
        $.getJSON('/protected/getfeed?locX=' + loc.coords.latitude + '&locY=' + loc.coords.longitude, function (res) {
            console.log(res);
            $.each(res, function (index, feedObject) {
                renderFeedObject(feedObject);
            })
        });
    });

    var renderFeedObject = function (feed) {
        var feedObjectType = feed.objectTypeId;
        var feedObjectTypeStr = feedObjectType == 0 ? 'Playdate' : 'Plats';
        var outputStr = '<div class="feed-content">' +
            '<div class="feed-image-composite">' +
            '<div class="feed-object-type object-type-'+ feedObjectType+'"><span>' + feedObjectTypeStr + '</span></div>' +
            '<img src="/protected/'+ feed.imageUrl+'">'+
            '<h1>'+feed.header+'</h1>' +
            '</div>' +
            '<p class="feed-object-description">'+ feed.shortDescription;
        if(feedObjectType == 0) {
            outputStr += '<a data-ajax="false" href="/protected/getoneplaydate?playdateId=' + feed.id + '">Visa Playdate</a>';
        } else if (feedObjectType == 1) {
            outputStr += '<a data-ajax="false" href="/protected/showplace?placeId=' + feed.id + '">Visa Plats</a>';
        }

        outputStr+= '</p></div>';
        outputDiv.append(outputStr);
    };

});
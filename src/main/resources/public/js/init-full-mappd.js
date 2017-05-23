$(document).ready(function () {



    function getLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(showPosition);
        } else {
            console.log("no navigator")
        }
    }

    function showPosition(pos) {
        var lat = pos.coords.latitude;
        var lng = pos.coords.longitude;
        window.getAndRenderPlaceForPos(lat, lng);
    }

    var clearMarkers = function(map) {
        for(var i = 0; i < window.markers.length; i++) {
            window.markers[i].setMap(map)
        }
    };

    var clearPlaces = function() {
        $('#playdate-output').html("");
        clearMarkers(null);
        window.markers = [];
    };

    window.getAndRenderPlaceForPos = function (lat, lng) {
        clearPlaces();

        $.getJSON('/protected/getpublicplaydatesbylocation?locX=' + lat + "&locY=" + lng, function (res) {
            console.log(res)
            if (res.length == 0) {
                var output = "<h3>Hittade inga platser här</h3><p>Testa att flytta kartan lite</p>"
                console.log("hittade inga platser")
                console.log("sätter output till = " + output);
                $('#playdate-output').html(output);
            } else {
                $.each(res, function (index, playdate) {
                    renderPlace(playdate, $('#playdate-output'));
                });
                $('div[data-role=collapsible]').collapsible();
            }
        });
    };




    getLocation();



});

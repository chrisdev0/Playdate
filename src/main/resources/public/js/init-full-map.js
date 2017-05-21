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
        $('#place-output').html("");
        clearMarkers(null);
        window.markers = [];
    };

    window.getAndRenderPlaceForPos = function (lat, lng) {
        clearPlaces();

        $.getJSON('/protected/getplacebylocation?locX=' + lat + "&locY=" + lng, function (res) {
            console.log(res)
            if (res.length == 0) {
                var output = "<h3>Hittade inga platser här</h3><p>Testa att flytta kartan lite</p>"
                console.log("hittade inga platser")
                console.log("sätter output till = " + output);
                $('#place-output').html(output);
            } else {
                $.each(res, function (index, place) {
                    renderPlace(place, $('#place-output'));
                });
                $('div[data-role=collapsible]').collapsible();
            }
        });
    };




    getLocation();



});

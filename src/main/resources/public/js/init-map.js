function initMap() {

    window.getLocation = function() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function(p){
                if(window.map != undefined) {
                    console.log("setting center to")
                    console.log(p.coords)
                    map.panTo(new google.maps.LatLng(p.coords.latitude, p.coords.longitude));
                } else {
                    console.log("map is undefined")
                }
            });
        } else {
            console.log("no navigator")
        }
    }


    var uluru = {lat: 59.299258865101194, lng: 18.044908470153807};
    window.map = new google.maps.Map(document.getElementById('map'), {
        zoom: 12,
        center: uluru
    });

    setTimeout(function () {
        console.log("running timeout");
        window.getLocation();
    }, 1500);


    window.markers = [];

    window.map.addListener('dragend', function () {
        var lat = this.getCenter().lat();
        var lng = this.getCenter().lng();
        window.getAndRenderPlaceForPos(lat, lng);
    });
}

function initPopupMap() {

    var ch = new CoordinateHandler();
    var latlng = ch.gridToGeodetic(parseInt(window.placeX), parseInt(window.placeY));

    var uluru = {lat: parseFloat(latlng.lat), lng: parseFloat(latlng.lon)};
    window.map = new google.maps.Map(document.getElementById('map'), {
        zoom: 10,
        center: uluru
    });


    var marker = new google.maps.Marker({
        position: uluru,
        title: ''
    });
    console.log(marker);
    marker.setMap(window.map);
    window.map.panTo(uluru);

}

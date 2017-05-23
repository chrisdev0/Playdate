/**
 * Created by martinsenden on 2017-05-19.
 */
$(document).ready(function() {

    window.showWeather = function(placeX, placeY){

        var ch = new CoordinateHandler;

        latlng = ch.gridToGeodetic(placeX, placeY);


        var lat = parseFloat(latlng.lat);
        var lon = parseFloat(latlng.lon);

        //Sätter längd på lat och lon till 6, pga det är max vi får stoppa in i SMHI
        lat = lat.toFixed(6);
        lon = lon.toFixed(6);

        var url = 'http://opendata-download-metfcst.smhi.se/api/category/pmp2g/version/2/geotype/point/lon/{lon}/lat/{lat}/data.json';

        console.log(url);

        url = url.replace("{lat}", lat);
        url = url.replace("{lon}", lon);
        $.getJSON(url, function(res){
            console.log(res);

            var dateOfPlaydate = parseInt(window.startTime) + 7200000;

            //console.log(dateOfPlaydate);
            var date = new Date(dateOfPlaydate);
            console.log(date.toJSON());
            date.setMinutes(00);
            date.setSeconds(00);
            date.setMilliseconds(0);
            console.log("ISO: "+ date.toISOString().substring(0,19)+'Z')
            var dateString = date.toISOString().substring(0,19)+'Z';
            var dateObject = checkDateOfPlaydate(dateString, res);

            console.log(dateObject)
            $("#show-weather").text(dateObject.parameters[1].values[0]);


        })

    }

    //setMinute, setSecond till 00:00
    function checkDateOfPlaydate(dateString, res){
        console.log("Här är objekt x inne i " + res.timeSeries[10].parameters[1].values[0] +
            " validTime: " + res.timeSeries[10].validTime + " dateString: " + dateString);
        console.log("Efter här kommer res")
        console.log(res.timeSeries);
        for(var i = 0; i < res.timeSeries.length; i++){
            if (res.timeSeries[i].validTime == dateString){
                console.log("korrekt");
                return res.timeSeries[i];
            }
        }


    }

})


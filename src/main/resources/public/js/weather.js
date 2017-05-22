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

            console.log(res.timeSeries[1].parameters[1].values[0]);

            var dateOfPlaydate = window.startTime;

            console.log(dateOfPlaydate);
            var date = new Date(parseInt(dateOfPlaydate));
            console.log(date.toUTCString());
            //var resArr = res;
            checkDateOfPlaydate(dateOfPlaydate, res);

        })

    }

    function checkDateOfPlaydate(dateOfPlaydate, res){
        console.log("Här är objekt x inne i " + res.timeSeries[1].parameters[1].values[0]);
        for(var i; i < res.length; i++){
            if (res.timeSeries[i].validTime == dateOfPlaydate){

            }
        }


    }

})


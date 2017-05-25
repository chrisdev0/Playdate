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
            //console.log(date.toJSON());
            date.setMinutes(00);
            date.setSeconds(00);
            date.setMilliseconds(0);
            //console.log("ISO: "+ date.toISOString().substring(0,19)+'Z')
            var dateString = date.toISOString().substring(0,19)+'Z';

            //checkIfWithinThreeDaysSpan(date);

            var dateObject = checkDateOfPlaydate(dateString, res);

            console.log(dateObject)
            if (dateObject === undefined){
                $("#show-weather").text("Ingen prognos finns");
            }
            else {
                $("#show-weather").append(dateObject.parameters[1].values[0] + '<i class="wi wi-celsius"></i>');
                var wSymb = dateObject.parameters[18].values[0];
                var wSymbol = checkWeatherSymbol(wSymb)
                $("#show-weathersymbol").append(wSymbol)

            }
        })

    }

    function checkIfWithinThreeDaysSpan(date){
        var currentDate = new Date();
        currentDate = currentDate.getTime();
        console.log("CD " + currentDate);
        console.log("dateobjekt: " + date.getTime());

        if (date.getTime() > (currentDate + 259200000)){
            if (date.getHours()){

            }
            console.log("jag är över datumet")
        }
        else{
            console.log("jag är under datumet")
        }

    }

    function checkDateOfPlaydate(dateString, res){
        console.log(res.timeSeries);
        for(var i = 0; i < res.timeSeries.length; i++){
            if (res.timeSeries[i].validTime === dateString){
                return res.timeSeries[i];
            }
        }
    }

    function checkWeatherSymbol(wSymb){
        if (wSymb === 1 || wSymb === 2){
            return '<i class="wi wi-day-sunny">'
        }

        if (wSymb === 3 || wSymb === 4 || wSymb === 5 || wSymb === 6 ) {
            return '<i class="wi wi-day-cloudy">'
        }

        if (wSymb === 7){
            return '<i class="wi wi-fog">'
        }

        if (wSymb === 8 || wSymb === 12) {
            return '<i class="wi wi-rain">'
        }

        if (wSymb === 9 || wSymb === 13) {
            return '<i class="wi wi-thunderstorm">'
        }

        if (wSymb === 10 || wSymb === 14) {
            return '<i class="wi wi-sleet">'
        }

        if (wSymb === 11 || wSymb === 15) {
            return '<i class="wi wi-snow">'
        }

        if (wSymb !== 1){
            return '<i class="wi wi-alien">'
        }
    }

    window.showCurrentWeather = function(placeX, placeY){

        var ch = new CoordinateHandler;

        latlng = ch.gridToGeodetic(placeX, placeY);


        var lat = parseFloat(latlng.lat);
        var lon = parseFloat(latlng.lon);

        //Sätter längd på lat och lon till 6, pga det är max vi får stoppa in i SMHI
        lat = lat.toFixed(6);
        lon = lon.toFixed(6);

        var url = 'https://opendata-download-metfcst.smhi.se/api/category/pmp2g/version/2/geotype/point/lon/{lon}/lat/{lat}/data.json';

        console.log(url);

        url = url.replace("{lat}", lat);
        url = url.replace("{lon}", lon);
        $.getJSON(url, function(res){
            console.log(res);

            var currentTime = new Date();

            currentTime = parseInt(currentTime.getTime()) + 7200000;

            currentTime = new Date(currentTime);
            currentTime.setMinutes(00);
            currentTime.setSeconds(00);
            currentTime.setMilliseconds(0);
            var dateString = currentTime.toISOString().substring(0,19)+'Z';

            var dateObject = checkDateOfPlaydate(dateString, res);

            console.log(dateObject)
            if (dateObject === undefined){
                $("#show-current-weather").text("Ingen prognos finns");
            }
            else {
                $("#show-current-weather").append(dateObject.parameters[1].values[0] + '<i class="wi wi-celsius"></i>');
                var wSymb = dateObject.parameters[18].values[0];
                var wSymbol = checkWeatherSymbol(wSymb)
                $("#show-weathersymbol").append(wSymbol)
            }
        })
    }
})


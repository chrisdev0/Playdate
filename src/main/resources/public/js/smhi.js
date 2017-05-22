/**
 * Created by martinsenden on 2017-05-19.
 */
$(document).ready(function() {

    function smhi () {

        /*
         The API end point. This is the only hardcoded url.
         All other urls are returned in responses.
         */
        smhi.endPoint = "https://opendata-download-metfcst.smhi.se/api/category/pmp2g/version/2/geotype/point/lon/{lon}/lat/{lat}/data.json";

        smhi.forecast = null;

        smhi.returnedLatitude = null;
        smhi.returnedLongitude = null;

        smhi.approvedTime = null;
        smhi.referenceTime = null;

        smhi.latitude = "58";
        smhi.longitude = "16";

        smhi.previousMarker = null;
        smhi.selectedParameter = null;

        /*
         Get the latest forecast - 'callback(forecast)' is called on completion. No error handling!
         */
        smhi.getForecast = function (callback) {

            var endPoint = smhi.endPoint.replace("{lat}", smhi.latitude).replace("{lon}", smhi.longitude);

            $.getJSON(endPoint).done(function (forecast) {

                smhi.forecast = forecast;
                window.hej = smhi;
                smhi.returnedLatitude = null;
                smhi.returnedLongitude = null;
                smhi.approvedTime = new Date(forecast.approvedTime).toISOString().replace(/[a-zA-Z]/g, ' ').substr(0, 16);
                smhi.referenceTime = new Date(forecast.referenceTime).toISOString().replace(/[a-zA-Z]/g, ' ').substr(0, 16);

                callback(forecast);
            });
        };

    }

smhi()

})
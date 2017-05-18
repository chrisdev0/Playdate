$(document).ready(function () {

    var myplaydatesoutput = $('#upcomming-playdates-output');
    var suggestedplaydatesoutput = $('#suggested-playdates-output');
    var playdatesclosebyoutput = $('#public-playdates-output');

    $.getJSON('/protected/getuserattentindplaydates', function (res){
        console.log("Result of get user attending playdates");
        console.log(res);
        renderPlaydates(res, myplaydatesoutput);
    });

    $.getJSON('/protected/getsuggestedplaydates', function (res) {
        console.log("Result of get suggested playdates");
        console.log(res);
        renderPlaydates(res, suggestedplaydatesoutput);
    });

    navigator.geolocation.getCurrentPosition(function(p){
        var x = p.coords.latitude;
        var y = p.coords.longitude;

        $.getJSON('/protected/getpublicplaydatesbylocation?locX=' + x + "&locY=" + y, function(res) {
            console.log("Result of get suggested playdates");
            console.log(res);
            renderPlaydates(res, playdatesclosebyoutput);
        })
    });


    var renderPlaydates = function(playdates, outputDiv) {
        var ooutput = "";
        $.each(playdates, function (index, playdate) {
            var output = '<div>' +
                '<p>' + playdate.header + '</p>' +
                '</div>';
            ooutput += output;
        });
        outputDiv.html(ooutput);
    };


});

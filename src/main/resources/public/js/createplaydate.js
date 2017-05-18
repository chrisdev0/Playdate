$(document).ready(function() {




    $('#place-search-type').change(function (e) {
        var searchtype = $('#place-search-type').val();
        $('.place-searcher-div').each(function (index, div) {
            var id = $(this).data('search-type-id');
            if (id == searchtype) {
                $(this).removeClass('hidden')
            } else {
                if (!$(this).hasClass('hidden')) {

                    $(this).addClass('hidden');
                }
            }
        });
    });

    function getParams() {
        var vars = [], hash;
        var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
        for(var i = 0; i < hashes.length; i++)
        {
            hash = hashes[i].split('=');
            vars.push(hash[0]);
            vars[hash[0]] = hash[1];
        }
        return vars;
    }

    var placeId = getParams()['placeId'];
    if(placeId != undefined) {
        $.getJSON('/protected/getplaceasjson?placeId=' + placeId, function (res) {
            console.log(res);
            $('#placeId').val(res.id);

            $('#show-place-placeholder').html(
                "<p class='name-with-button'>" + res.name + "</p>" +
                "<a href='/protected/showplace?placeId=" + res.id +"'>Visa</a>"
            );

        });
    }


    var searchField = $('#place-input-field');

    var searchSuggestionts = $('#search-suggestions');

    searchField.on('input', function () {
        var text = $(this).val();
        if (text.length > 3) {
            console.log("sending search for " + text);
            $.getJSON('/protected/searchplacebyterm?searchTerm=' + text, {}, function (data) {
                var suggestion = "";
                //console.log(data);
                $.each(data, function (index, place) {
                    suggestion += "<li class='place-list-object' data-place-name=" + place.name + " data-place-id=" + place.id + ">" + place.name + "</li>"
                });
                console.log(suggestion);
                searchSuggestionts.html(suggestion);
                searchSuggestionts.listview("refresh");
            })
        }
    });

    searchSuggestionts.on('click',".place-list-object",function(e) {
        var placeId = $(this).data('placeId');
        var placeName = $(this).data('placeName');
        $('#show-place-placeholder').html(
            "<p class='name-with-button'>" + placeName + "</p>" +
            "<a href='/protected/showplace?placeId=" + placeId +"'>Visa</a>"
        );
        $('#placeId').val(placeId);
        searchSuggestionts.html('');
        searchSuggestionts.listview("refresh");
        searchField.val("");
    });

    $('#create-playdate-form').on('submit',function(e) {
        var starttime = $('#startTime');
        var date = $('#datepicker-field').datebox('getTheDate');
        var time = $('#timepicker-field').datebox('getTheDate');
        date.setHours(time.getHours());
        date.setMinutes(time.getMinutes());
        console.log("setting start time to " + date.getTime());
        starttime.val(date.getTime());
        console.log("starttimevalue = " + starttime.val());
    });

    $('#create-playdate-btn').click(function (e) {
        e.preventDefault();

        $.ajax({
            type: 'POST',
            url: $('#create-playdate-form').attr('action'),
            data: $('#create-playdate-form').serialize(),
            success: function(e) {
                console.log("created playdate")
            },
            error: function(e) {
                console.log(e)
            }
        })

    });


});

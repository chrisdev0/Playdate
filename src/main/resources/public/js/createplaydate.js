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
            $.getJSON('/protected/searchplacebyterm?searchTerm=' + text, {}, function (data) {
                var suggestion = "";
                $.each(data, function (index, place) {
                    suggestion += "<li class='place-list-object' data-place-name=" + place.name + " data-place-id=" + place.id + ">" + place.name + "</li>"
                });
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

    $('#place-output').on('click', '.choose-place-link', function (e) {
        e.preventDefault();
        console.log("inside välj plats");
        var placeId = $(this).data('placeId');
        var placeName = $(this).data('placeName');
        $('#show-place-placeholder').html(
            "<p class='name-with-button'>" + placeName + "</p>" +
            "<a href='/protected/showplace?placeId=" + placeId +"'>Visa</a>"
        );
        $('#placeId').val(placeId);
        $(':mobile-pagecontainer').pagecontainer('change', '#pageone');
    });


    var convertStartTime = function() {
        var starttime = $('#startTime');
        var date = $('#datepicker-field').datebox('getTheDate');
        var time = $('#timepicker-field').datebox('getTheDate');
        date.setHours(time.getHours());
        date.setMinutes(time.getMinutes());
        starttime.val(date.getTime());
    };

    $('#create-playdate-btn').click(function (e) {
        e.preventDefault();
        convertStartTime();
        $.ajax({
            type: 'POST',
            url: $('#create-playdate-form').attr('action'),
            data: $('#create-playdate-form').serialize(),
            success: function(e) {
                console.log("created playdate")
                window.location.replace(e);
            },
            error: function(e) {
                var error = e.responseText;
                console.log(error);
                if(error.substr(0,6) === 'valida'){
                    error = error.replace('validation_error_', '');
                    var split = error.split("_");
                    var errorsMsg = "";
                    for(var i = 0; i < split.length; i++) {
                        if (split[i] === 'description') {
                            errorsMsg += "Beskrivningen måste vara mellan 10 och 300 tecken lång<br>"
                        }
                        if (split[i] === 'header') {
                            errorsMsg += "Rubriken måste vara 3 och 30 tecken lång<br>"
                        }
                        if (split[i] === 'place') {
                            errorsMsg += "Platsen du valt kunde inte hittas<br>"
                        }
                        if (split[i] === 'starttime'){
                            errorsMsg += "Starttiden du valt ligger för för långt fram eller har redan hänt";
                        }
                    }
                    console.log(errorsMsg);
                    $('#validationPopup p').html(errorsMsg);
                    $('#validationPopup').popup('open');
                }
            }
        })

    });


});

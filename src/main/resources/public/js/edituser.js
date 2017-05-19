$(document).ready(function() {
    $('#edit-profile-picture').click(function() {
        $('#popup').popup("open");
    })

    $('#genderselect').val(window.genderid).change();

    $('#picturefile').change(function(e) {
        var file = e.target.files[0];
        if (file.type.match('image.*')) {
            var reader = new FileReader();
            reader.onload = (function(image) {
                return function(e) {
                    var i = e.target.result;
                    console.log('init croppie')
                    window.croppie = $('#croppie').croppie({
                        viewport: {
                            width: 260,
                            height: 260
                        },
                        boundary: {
                            width: 300,
                            height: 300
                        },
                        url: i
                    });
                    console.log('loaded url')
                    $('#upload-div').hide()

                };
            })(file);

            reader.readAsDataURL(file);

        }
    })


    $('#upload-picture').click(function() {
        var picture = window.croppie.croppie('result',
            {type: 'blob',format: 'png', size: 'viewport'})
            .then(function(res) {
                sendImage(res)
            });
    });

    var sendImage = function(image){
        var form = new FormData();
        form.append('profile_picture_file', image)
        $.ajax({
            type: 'POST',
            url: '/protected/postnewprofilepicture',
            data: form,
            cache: false,
            contentType: false,
            processData: false
        }).done(function(data){
            console.log(data)
            $('.profile-picture-upload').attr('src', data);
            $('#popup').popup('close');
        })
    }

    $('#update-profile-btn').on('click',function(e) {
        e.preventDefault();

        $.ajax({
            type: 'POST',
            url: $('#update-profile-form').attr('action'),
            data: $('#update-profile-form').serialize(),
            success: function (e) {
                window.location.replace(e)
            },
            error: function (e) {
                console.log(e)
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
                        if (split[i] === 'phone') {
                            errorsMsg += "Felaktigt telefonnummer<br>"
                        }
                    }
                    console.log(errorsMsg);
                    $('#validationPopup p').html(errorsMsg);
                    $('#validationPopup').popup('open');
                }
            }
        });
    })
});
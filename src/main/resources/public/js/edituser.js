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

    $('#update-profile-btn').one('click',function(e) {
        e.preventDefault();
        $('#update-profile-form').submit();
    })
});
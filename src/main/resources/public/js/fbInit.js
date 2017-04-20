window.fbAsyncInit = function() {
    FB.init({
        appId      : 'FBAPPIDTOKEN',
        cookie     : true,
        xfbml      : true,
        version    : 'v2.8'
    });
    FB.AppEvents.logPageView();
    FB.getLoginStatus(function(response) {
        console.log(response);
        if(response.status === "not_authorized" || response.status === "unknown"){
            console.log("running fb login");
            FB.login(function(response){
                console.log(response);
                FB.api('/me?fields=id,name,email,picture{url}', function(response) {
                    console.log("user info");
                    console.log(response);
                    $('#output').text("Hello " + response.name + ".");
                })
            },{scope: 'public_profile,email'})
        } else {
            FB.api('/me?fields=id,name,email,picture{url}', function(response) {
                console.log("user info");
                console.log(response);
                $('#output').text("Hello " + response.name + ".");
            })
        }
    });
};

(function(d, s, id){
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) {return;}
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));


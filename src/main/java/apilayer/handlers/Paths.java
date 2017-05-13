package apilayer.handlers;

public class Paths {

    public static final String PROTECTED = "/protected";


    public static final String GETONEPLACE = "/getoneplace";

    public static final String POSTCOMMENT = "/postcomment";

    public static final String LOGOUT = "/logout";


    public static final String CREATEPLAYDATE = "/createplaydate";
    public static final String GETONEPLAYDATE = "/getoneplaydate";

    public static final String EDITPROFILE = "/editmyprofile";
    public static final String DELETEPLAYDATE = "/deleteplaydate";

    public static final String DELETECOMMENT = "/deletecomment";


    public static final String FBCALLBACK = "/callback";
    public static final String SHOWPLAYDATES = "/showplaydates";

    public static final String APIIMAGE = "/apiimage";

    public static final String UPDATEPLAYDATE = "/updateplaydate";

    public static final String PLAYDATE_IMAGE = "/playdateimage";
    public static final String GETPLACEBYLOCATION = "/getplacebylocation";
    public static final String GETPLACEBYNAME = "/getplacebyname";
    public static final String GETPLACEBYGEONAME = "/getplacebygeoarea";
    public static final String POSTNEWPROFILEPICTURE = "/postnewprofilepicture";
    public static final String GETPROFILEPICTURE = "/getprofilepicture";
    public static final String GETFEED = "/getfeed";
    public static final String LANDING = "/feed";
    public static final String SHOWPLACE = "/showplace";
    public static final String SEARCH_PLACE_BY_TERM = "/searchplacebyterm";
    public static final String SHOWUSER = "/showuser";
    public static final String EDITPLAYDATE = "/editplaydate";
    public static final String GETMYFRIENDS = "/showmyfriends";
    public static final String ACCEPTFRIENDSHIPREQUEST = "/acceptfriendshiprequest";
    public static final String DECLINEFRIENDSHIPREQUEST = "/declinefriendshiprequest";
    public static final String GETMYINVITES = "/getmyinvites";
    public static final String ACCEPTINVITE = "/acceptinvite";
    public static final String DECLINEINVITE = "/declineinvite";
    public static final String FINDPLACE = "/findplaces";

    public static class StaticFilePaths {
        public static final String INDEX_HTML = "/index.html";
    }

    public static class QueryParams {
        public static final String PLAYDATE_BY_ID = "playdateId";
        public static final String PLACE_BY_ID = "placeId";
        public static final String USER_BY_ID = "userId";
        public static final String COMMENT_BY_ID = "commentId";
        public static final String INVITE_BY_ID = "inviteId";
        public static final String COMMENT_CONTENT = "comment";

        public static final String INVITE_MSG = "inviteMessage";
    }
}

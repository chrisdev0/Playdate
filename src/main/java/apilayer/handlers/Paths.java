package apilayer.handlers;

public class Paths {

    public static final String PROTECTED = "/protected";

    public static final String GETALLPLAYDATES = "/getallplaydates";
    public static final String GETALLPLACE = "/getallplace";
    public static final String GETONEPLACE = "/getoneplace";
    public static final String POSTCOMMENT = "/postcomment";
    public static final String LOGOUT = "/logout";
    public static final String TRYLOGIN = "/trylogin";
    public static final String CREATEPLAYDATE = "/createplaydate";
    public static final String GETONEPLAYDATE = "/getoneplaydate";
    public static final String CREATEPROFILE = "/createprofile";
    public static final String SHOWPROFILE = "/showprofile";
    public static final String DELETEPLAYDATE = "/deleteplaydate";
    public static final String DELETECOMMENT = "/deletecomment";
    public static final String DOREG = "/doreg";
    public static final String FBCALLBACK = "/callback";
    public static final String SHOWPLAYDATE = "/showplaydate";

    public static final String APIIMAGE = "/apiimage";

    public static final String PLAYDATE_IMAGE = "/playdateimage";
    public static final String GETPLACEBYLOCATION = "/getplacebylocation";
    public static final String GETPLACEBYNAME = "/getplacebyname";


    public static class StaticFilePaths {
        public static final String INDEX_HTML = "/index.html";
    }

    public static class QueryParams {
        public static final String GET_ONE_PLAYDATE_BY_ID = "playdateId";
        public static final String GET_ONE_PLACE_BY_ID = "placeId";
    }
}

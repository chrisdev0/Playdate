package stockholmapi;

public interface APIDownloader {


    void downloadFromAPI(String APIId, String apiName) throws Exception;

    void loadGeoAreas() throws Exception;

    boolean isFinished();

}

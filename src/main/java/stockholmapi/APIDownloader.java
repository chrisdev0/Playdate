package stockholmapi;

public interface APIDownloader {


    void downloadFromAPI(String APIId) throws Exception;

    void loadGeoAreas() throws Exception;

    boolean isFinished();

}

package stockholmapi;

import dblayer.PlaceDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import model.Place;
import stockholmapi.jsontojava.DetailedServiceUnit;
import stockholmapi.jsontojava.ServiceUnitTypes;

import java.util.List;
import java.util.stream.Collectors;

public class APIDownloaderImpl implements APIDownloader {

    private static int ID_COUNTER = 0;
    private int id = ID_COUNTER++;
    private boolean finished = false;

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void downloadFromAPI(String apiId) throws Exception {
        doLoadDuringRun(apiId);
        finished = true;
    }


    @Override
    public void loadGeoAreas() throws Exception {
        APIDataLoader.loadGeoAreas();
        finished = true;
    }

    private void doLoadDuringRun(String serviceGuideServiceTypeId) throws Exception{
        ServiceUnitTypes[] placesToLoad = new ServiceGuideServiceLoader().load(serviceGuideServiceTypeId);
        List<DetailedServiceUnit> load = new DetailedServiceGuideServiceLoader().load(placesToLoad, serviceGuideServiceTypeId);
        PlaceDAO.getInstance().updatePlaceFromAPIEndPoint(load.stream().map(Place::constructFromDetailedServiceUnit).collect(Collectors.toList()));
    }

}

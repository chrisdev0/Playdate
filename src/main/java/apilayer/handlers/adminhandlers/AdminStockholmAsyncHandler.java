package apilayer.handlers.adminhandlers;

import apilayer.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;
import stockholmapi.APIDownloader;
import stockholmapi.APIDownloaderImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static apilayer.handlers.asynchandlers.SparkHelper.setStatusCodeAndReturnString;

@Slf4j
public class AdminStockholmAsyncHandler {


    public static Map<String, Runner> runners = new ConcurrentHashMap<>();

    public static Object reloadAPI(Request request, Response response) {
        String apiKey = request.queryParams("apiId");
        APIDownloader apiDownloader = new APIDownloaderImpl();
        if (runners.containsKey(apiKey)) {
            return setStatusCodeAndReturnString(response, 400, Constants.ADMIN.RUNNER_ALREADY_RUNNING);
        }
        runners.put(apiKey, new Runner(apiKey));
        return setStatusCodeAndReturnString(response, 200, apiKey);
    }


    public static Object getStatusOfRunner(Request request, Response response) {
        String runnerKey = request.queryParams("runnerId");
        if (runners.containsKey(runnerKey)) {
            if (runners.get(runnerKey).apiDownloader.isFinished()) {
                return setStatusCodeAndReturnString(response, 200, "finished");
            } else {
                return setStatusCodeAndReturnString(response, 400, "running");
            }
        } else {
            return setStatusCodeAndReturnString(response, 400, "no");
        }
    }


    @Data
    private static class Runner extends Thread {
        String apikey;
        APIDownloader apiDownloader;

        public Runner(String apikey) {
            this.apikey = apikey;
            start();
        }

        @Override
        public void run() {

            apiDownloader = new APIDownloaderImpl();
            try {
                apiDownloader.downloadFromAPI(apikey);
                log.info("finished loading api");
            } catch (Exception e) {
                log.error("error loading api",e);
            }

            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runners.remove(apikey);
        }
    }
}

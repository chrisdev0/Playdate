package apilayer.handlers.adminhandlers;

import apilayer.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
        String apiName = request.queryParams("apiName");
        log.info("api key = " + apiKey + " api name  = " + apiName);
        if (runners.containsKey(apiKey)) {
            return setStatusCodeAndReturnString(response, 400, Constants.ADMIN.RUNNER_ALREADY_RUNNING);
        }
        runners.put(apiKey, new Runner(apiKey, apiName));
        return setStatusCodeAndReturnString(response, 200, apiKey);
    }


    public static Object getStatusOfRunner(Request request, Response response) {
        String runnerKey = request.queryParams("runnerId");
        if (runners.containsKey(runnerKey)) {
            if (runners.get(runnerKey).apiDownloader.isFinished()) {
                return setStatusCodeAndReturnString(response, 200, "finished");
            } else {
                return setStatusCodeAndReturnString(response, 200, "running");
            }
        } else {
            return setStatusCodeAndReturnString(response, 400, "no");
        }
    }



    private static class Runner extends Thread {
        String apikey, apiName;
        APIDownloader apiDownloader;

        public Runner(String apikey, String apiName) {
            this.apikey = apikey;
            this.apiName = apiName;
            start();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Runner runner = (Runner) o;

            return apikey != null ? apikey.equals(runner.apikey) : runner.apikey == null;
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = 31 * result + (apikey != null ? apikey.hashCode() : 0);
            return result;
        }

        @Override
        public void run() {

            apiDownloader = new APIDownloaderImpl();
            try {
                apiDownloader.downloadFromAPI(apikey, apiName);
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

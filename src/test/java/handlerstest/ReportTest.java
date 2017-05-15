package handlerstest;

import apilayer.Constants;
import apilayer.handlers.Paths;
import apilayer.handlers.asynchandlers.ReportHandler;
import model.User;
import org.junit.Test;
import spark.Request;
import spark.Response;
import testutils.MockTestHelpers;

import static org.junit.Assert.assertTrue;
import static testutils.ModelCreators.*;

public class ReportTest extends MockTestHelpers {

    @Test
    public void testCreateReport() {
        User reporter = createUser();
        User badUser = createUser();
        save(reporter);
        save(badUser);

        Request request = initRequestMock(reporter);
        Response response = initResponseMock();

        injectKeyValue(request,
                new KeyValue(Paths.QueryParams.USER_BY_ID, badUser.getId()),
                new KeyValue(Paths.QueryParams.REPORT_DESCRIPTION, "rapport")
        );

        String res = (String) ReportHandler.createUserReport(request, response);

        assertTrue(res.equals(Constants.MSG.OK));
        
        remove(reporter);
        remove(badUser);
    }

}

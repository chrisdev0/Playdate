package dbtest;

import dblayer.ReportDAO;
import model.Report;
import model.User;
import org.junit.Test;
import testhelpers.HibernateTests;
import testutils.ModelCreators;
import static org.junit.Assert.*;
import java.util.Optional;

/**
 * Created by martinsenden on 2017-05-11.
 */
public class ReportDAOTest extends HibernateTests {

    @Test
    public void testReportUser(){
        User reporter = ModelCreators.createUser();
        User reportedUser = ModelCreators.createUser();
        String description = faker.lorem().sentence();

        ModelCreators.save(reporter);
        ModelCreators.save(reportedUser);

        Optional<Report> report = ReportDAO.createUserReport(reporter, reportedUser, description);
        assertTrue(report.isPresent());

        ModelCreators.remove(reporter);
        ModelCreators.remove(reportedUser);

    }

    @Test
    public void testReportNonExistingUser(){

    }

}

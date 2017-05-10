package apilayer.handlers.deprecated;

import apilayer.Constants;
import apilayer.StaticFileTemplateHandler;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.Gender;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import spark.HaltException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import utils.ParserHelpers;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.halt;

@Slf4j
public class ProfileHandlerOLD extends StaticFileTemplateHandler{

    public ProfileHandlerOLD(String templateName, int onErrorHTTPStatusCode) throws IllegalArgumentException {
        super(templateName, onErrorHTTPStatusCode, true);
    }

    @Override
    public ModelAndView handleTemplateFileRequest(Request request, Response response) {
        String name = request.queryParams("name");
        String description = request.queryParams("description");
        String phoneNumber = request.queryParams("phone");
        phoneNumber = Utils.removeNonDigitChars(phoneNumber);
        String genderStr = request.queryParams("gender");
        User user = request.session().attribute(Constants.USER_SESSION_KEY);
        Gender gender = null;
        try {
            gender = Gender.genderIdToGender(ParserHelpers.parseToInt(genderStr));
        } catch (HaltException e) {
            throw e;
        }

        if (user == null || !userInformationIsCorrect(name, description, phoneNumber)) {
            log.error("userinformation was invalid");
            throw halt(400);
        }
        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setGender(gender);
        user.setDescription(description);
        Transaction tx = null;
        try (Session session = HibernateUtil.getInstance().openSession()) {
            tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            log.error("error updating user", e);
            throw halt(400);
        }
        return super.handleTemplateFileRequest(request, response);
    }

    private boolean userInformationIsCorrect(String name, String description, String phoneNumber) {
        return Utils.isValidPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<Map<String, Object>> createModelMap(Request request) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", request.session().attribute(Constants.USER_SESSION_KEY));
        return Optional.of(map);
    }
}

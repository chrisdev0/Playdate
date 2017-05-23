package stockholmapi;

import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.GeographicalArea;
import model.Place;
import org.hibernate.Session;
import org.hibernate.Transaction;
import secrets.Secrets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static stockholmapi.helpers.APIUtils.getUrl;

@Slf4j
public class APILoader {

    private HibernateUtil hibernateUtil;
    private List<Object> toAddToDB;

    public APILoader() {
        hibernateUtil = HibernateUtil.getInstance();
        toAddToDB = new ArrayList<>();
    }

    @Deprecated
    public void doLoadOnStartup(String serviceGuideServiceTypeId) throws Exception {


    }



}

package dblayer.admin;

import apilayer.Constants;
import dblayer.HibernateUtil;
import org.hibernate.Session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DashboardDAO {
    
    private static DashboardDAO instance;

    private DashboardDAO(){}

    public static DashboardDAO getInstance() {
        if (instance == null) {
            instance = new DashboardDAO();
        }
        return instance;
    }
    
    
    enum COUNTS {
        REPORT("SELECT COUNT(*) FROM Report",Constants.ADMIN.DASHBOARD.REPORT_COUNTS),
        USERS("SELECT COUNT(*) FROM User",Constants.ADMIN.DASHBOARD.USER_COUNTS),
        PLACE("SELECT COUNT(*) FROM Place",Constants.ADMIN.DASHBOARD.PLACE_COUNTS),
        PLAYDATE("SELECT COUNT(*) FROM Playdate",Constants.ADMIN.DASHBOARD.PLAYDATE_COUNTS);

        public String hql;
        public String mapKey;

        COUNTS(String hql, String mapKey) {
            this.hql = hql;
            this.mapKey = mapKey;
        }
    }

    public Map<String, Long> getDashboardCounts() {
        Map<String, Long> counts = new HashMap<>();

        try (Session session = HibernateUtil.getInstance().openSession()) {
            Stream.of(COUNTS.values()).forEach(c -> putValueInMap(session, counts, c.hql, c.mapKey));
        }

        return counts;
    }

    private void putValueInMap(Session session, Map<String, Long> map, String hql, String mapKey) {
        map.put(mapKey, (Long) session.
                createQuery(hql)
                .uniqueResult());
    }


}

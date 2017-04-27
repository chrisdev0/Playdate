package webservertest;


import apilayer.handlers.Paths;
import dblayer.HibernateUtil;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/** Innehåller tester som säkerställer så att
 *
 *  Skapa playdate
 *  Ta bort playdate
 *
 *  fungerar
 *
 *  servern måste vara uppstartad när testen körs igenom för att fungera
 * */
@Slf4j
public class Tests {


    private static String sessionCookie;
    private static List<String> playdatesToRemove = new ArrayList<>();

    @BeforeClass
    public static void getSessionCookie() throws Exception{
        Connection.Response res = Jsoup.connect("http://localhost:9000/trylogin")
                .data("username", "a@b.com")
                .data("password", "password").method(Connection.Method.POST).execute();
        sessionCookie = res.cookie("JSESSIONID");
    }

    @Test
    public void testCreatePlaydate() throws Exception {
        Connection.Response res = Jsoup.connect("http://localhost:9000/protected" + Paths.CREATEPLAYDATE)
                .cookie("JSESSIONID", sessionCookie)
                .data(createPlaydateMap())
                .method(Connection.Method.POST)
                .execute();
        String id = getIdFromResponse(res);
        playdatesToRemove.add(id);
        assertEquals(res.statusCode(), 200);
    }

    public static Connection.Response deletePlaydate(String id) throws Exception{
        return Jsoup.connect("http://localhost:9000/protected" + Paths.DELETEPLAYDATE)
                .cookie("JSESSIONID", sessionCookie)
                .data("playdateId", "" + id)
                .method(Connection.Method.DELETE)
                .followRedirects(true)
                .execute();
    }

    public String getIdFromResponse(Connection.Response res) {
        return res.url().toString().substring(res.url().toString().lastIndexOf("=") +1);
    }

    public Map<String, String> createPlaydateMap() {
        Map<String, String> map = new HashMap<>();
        map.put("placeId", "5");
        map.put("startTime", "123");
        map.put("endTime", "123");
        map.put("visibilityId", "0");
        map.put("header", "titel finns här");
        map.put("description", "beskrivning finns här");
        return map;
    }

    @Test
    public void testRemovePlaydate() throws Exception {
        Connection.Response res = Jsoup.connect("http://localhost:9000/protected" + Paths.CREATEPLAYDATE)
                .cookie("JSESSIONID", sessionCookie)
                .data(createPlaydateMap())
                .method(Connection.Method.POST)
                .followRedirects(true)
                .execute();

        String idFromResponse = getIdFromResponse(res);
        log.info("id of response = " + idFromResponse);
        assertEquals(200, deletePlaydate(idFromResponse).statusCode());
    }

    @AfterClass
    public static void deletePlaydates() {
        playdatesToRemove.forEach(s -> {
            try {
                deletePlaydate(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

package webservertest;

import apilayer.WebServer;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Request;
import spark.Spark;

@Slf4j
public class Tests {

    @BeforeClass
    public static void initSpark() {
        log.info("Starting spark for test");
        new WebServer();
        Spark.awaitInitialization();
        log.info("Spark is running");
    }

    @Test
    public void test() throws Exception{
        Connection connection = Jsoup.connect("http://localhost:9000/trylogin")
                .data("username","a@b.com")
                .data("password","password");
        Document document = connection.get();
        log.info(document.head().data());
    }

    @AfterClass
    public static void endSpark() {
        log.info("stopping spark");
        Spark.stop();
    }

}

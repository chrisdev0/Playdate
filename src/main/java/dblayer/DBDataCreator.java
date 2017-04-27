package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Slf4j
public class DBDataCreator {

    /** Lägger till lite testdata
     *  todo    Flytta till egen klass och skapa devdata för hela modellen
     * */
    public static void initDEVData() {
        /*
        User user = new User("abc", "Hej Hejsan", "a@b.com", "password", "123", "..", Gender.FEMALE);
        user = User.createUserHelper(user, "password");
        Child child = new Child(18, Gender.FEMALE, user);
        user.addChild(child);
        User user2 = new User("abc", "Nils Svensson", "hej@b.com", "password", "1231", "..", Gender.MALE);
        user2 = User.createUserHelper(user2, "password2");
        Place place = new Place("abc-123", "Testlekplats", "Testlekplats ligger i aula nod på DSV", "images/testlekplats.png", "123", "123", 10, 10, "");
        Comment comment = new Comment("Bästa stället i stockholm", user, false);
        Comment comment2 = new Comment("Bättre än L50, sämre än L30. Brukar gå hit med min son Bengt-Fridolf för att lyssna på föreläsningar om UML", user2, false);
        Playdate playdate = new Playdate("Hej", "blbl", 123, 321, user, place, PlaydateVisibilityType.intToPlaydateVisibilityType(0));
        Invite invite = new Invite("Hej", false, playdate, user2);
        place.addComment(comment);
        place.addComment(comment2);
        try (Session session = HibernateUtil.getInstance().openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(user);
            session.save(invite);
            session.save(user2);
            session.save(child);
            session.save(place);
            session.save(comment);
            session.save(comment2);
            session.save(playdate);
            tx.commit();
        } catch (Exception e) {
            log.error("hibernate error", e);
        }
        */
    }

}

package dblayer;

import lombok.extern.slf4j.Slf4j;
import model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Slf4j
public class DBDataCreator {

    /** Lägger till lite testdata
     * */
    public static void initDEVData() {
        User user = new User("Hej Hejsan", "a@b.com", "password", "123", "..", Gender.FEMALE);
        user = User.createUserHelper(user, "password");
        User user2 = new User("Nils Svensson", "hej@b.com", "password", "1231", "..", Gender.MALE);
        user2 = User.createUserHelper(user2, "password2");
        User user3 = User.createUserHelper(new User("Göran baconsson", "g@b.com", null, "23123", "..", Gender.OTHER), "password2");
        Place place = new Place("abc-123", "Testlekplats", "Testlekplats ligger i aula nod på DSV", "images/testlekplats.png", "123", "123", 10, 10, "");
        Comment comment = new Comment("Bästa stället i stockholm", user, false);
        Comment comment2 = new Comment("Bättre än L50, sämre än L30. Brukar gå hit med min son Bengt-Fridolf för att lyssna på föreläsningar om UML", user2, false);
        Playdate playdate = new Playdate("Hej", "blbl", 123, 321, user, place, PlaydateVisibilityType.intToPlaydateVisibilityType(0));
        playdate.addParticipant(user3);
        Invite invite = new Invite("Hej", false, playdate, user2);
        place.addComment(comment);
        place.addComment(comment2);
        try (Session session = HibernateUtil.getInstance().openSession()) {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(user);
            session.saveOrUpdate(invite);
            session.saveOrUpdate(user2);
            session.saveOrUpdate(user3);
            session.saveOrUpdate(place);
            session.saveOrUpdate(comment);
            session.saveOrUpdate(comment2);
            session.saveOrUpdate(playdate);
            tx.commit();
        } catch (Exception e) {
            log.error("hibernate error", e);
        }

    }

}

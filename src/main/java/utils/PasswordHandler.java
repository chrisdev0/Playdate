package utils;
import apilayer.Constants;
import com.google.common.hash.Hashing;
import model.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.MessageDigest;

/**
 * Created by martinsenden on 2017-04-24.
 */
public class PasswordHandler {

    public static String generateUserSalt(){
        /*
        Sätt användarens salt och spara i DB
         */

        return RandomStringUtils.randomAlphanumeric(Constants.LENGTH_OF_SALT);
    }


    public static String hashUserPwd(String input, String saltString){
        /*
        Hasha.
         */
        input += saltString;
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }

    private boolean validateUserPwd(User u, String rawPassword) {
        /*
        Hämta hash och salt från DB och kolla om equals.
        */

        String salt = u.getSalt();
        String hashedRawPwd = hashUserPwd(rawPassword, salt);

        return hashedRawPwd.equals(u.getPassword());

    }

}

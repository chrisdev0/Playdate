package utils;
import apilayer.Constants;
import com.google.common.hash.Hashing;
import model.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.MessageDigest;


public class PasswordHandler {

    public static String generateUserSalt(){
        return RandomStringUtils.randomAlphanumeric(Constants.LENGTH_OF_SALT);
    }

    public static String hashUserPwd(String input, String saltString){
        input += saltString;
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }

    public static boolean validateUserPwd(User u, String rawPassword) {
        String salt = u.getSalt();
        String hashedRawPwd = hashUserPwd(rawPassword, salt);
        return hashedRawPwd.equals(u.getPassword());
    }

}

package helper;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Encryption {

    public static String bcryptHash(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean comparePassword(String password, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hash);
        return result.verified;
    }
}

package bg.sofia.uni.fmi.mjt.vault.server.user.model;

import bg.sofia.uni.fmi.mjt.vault.crypto.EncryptedData;
import bg.sofia.uni.fmi.mjt.vault.util.Validator;

import java.io.Serializable;

public record Registration(String website, String user, EncryptedData data) implements Serializable {

    public static Registration of(String website, String user, EncryptedData data) {
        Validator.validateString("Website", website);
        Validator.validateString("User", user);
        Validator.requiredNonNull("EncryptedData", data);

        return new Registration(website, user, data);
    }
}

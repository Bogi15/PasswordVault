package bg.sofia.uni.fmi.mjt.vault.crypto;

import java.io.Serializable;

public record EncryptedData(String cipherText, String iv, String salt) implements Serializable {
}

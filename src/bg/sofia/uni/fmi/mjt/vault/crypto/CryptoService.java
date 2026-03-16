package bg.sofia.uni.fmi.mjt.vault.crypto;

public interface CryptoService {
    EncryptedData encrypt(String plainText, String masterPassword);

    String decrypt(EncryptedData encryptedData, String masterPassword);
}

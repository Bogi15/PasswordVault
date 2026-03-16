package bg.sofia.uni.fmi.mjt.vault.crypto;

import bg.sofia.uni.fmi.mjt.vault.exceptions.DecryptionFailedException;
import bg.sofia.uni.fmi.mjt.vault.exceptions.EncryptionFailedException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESCryptoService implements CryptoService {

    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 16;
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";
    private static final String KEY_FACTORY_INSTANCE = "PBKDF2WithHmacSHA256";

    @Override
    public EncryptedData encrypt(String plainText, String masterPassword) {
        try {
            SecureRandom random = new SecureRandom();

            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            SecretKey key = deriveKey(masterPassword, salt);

            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return new EncryptedData(Base64.getEncoder().encodeToString(encrypted),
                                     Base64.getEncoder().encodeToString(iv),
                                     Base64.getEncoder().encodeToString(salt));

        } catch (Exception e) {
            throw new EncryptionFailedException("Encryption failed", e);
        }
    }

    @Override
    public String decrypt(EncryptedData encryptedData, String masterPassword) {
        try {
            byte[] salt = Base64.getDecoder().decode(encryptedData.salt());
            byte[] iv = Base64.getDecoder().decode(encryptedData.iv());
            byte[] cipherText =  Base64.getDecoder().decode(encryptedData.cipherText());

            SecretKey key = deriveKey(masterPassword, salt);

            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            byte[] decrypted = cipher.doFinal(cipherText);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new DecryptionFailedException("Decryption failed", e);
        }
    }

    private SecretKey deriveKey(String password, byte[] salt) throws Exception {

        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_FACTORY_INSTANCE);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        SecretKey tmp = factory.generateSecret(spec);

        return new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
    }
}
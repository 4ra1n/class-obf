package me.n1ar4.templates;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESTemplates {
    private static final String key = "1234567890123456";

    public static String encrypt(String data, String key) throws Exception {
        key = new StringBuilder(key).reverse().toString();
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
        String base = Base64.getEncoder().encodeToString(encryptedBytes);
        return new StringBuilder(base).reverse().toString();
    }

    public static String decrypt(String encryptedData, String key) {
        try {
            key = new StringBuilder(key).reverse().toString();
            encryptedData = new StringBuilder(encryptedData).reverse().toString();
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            Class<?> base64;
            byte[] value = null;
            try {
                base64 = Class.forName("java.util.Base64");
                Object decoder = base64.getMethod("getDecoder", null).invoke(base64, null);
                value = (byte[]) decoder.getClass().getMethod("decode",
                        new Class[]{String.class}).invoke(decoder, new Object[]{encryptedData});
            } catch (Exception e) {
                try {
                    base64 = Class.forName("sun.misc.BASE64Decoder");
                    Object decoder = base64.newInstance();
                    value = (byte[]) decoder.getClass().getMethod("decodeBuffer",
                            new Class[]{String.class}).invoke(decoder, new Object[]{encryptedData});
                } catch (Exception ex) {
                    return "";
                }
            }
            byte[] decryptedBytes = cipher.doFinal(value);
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception ignored) {
            return "";
        }
    }

    public static void main(String[] args) {
        try {
            String originalData = "Hello, AES Encryption!";
            String encryptedData = encrypt(originalData, key);
            System.out.println("Encrypted: " + encryptedData);
            String decryptedData = decrypt(encryptedData, key);
            System.out.println("Decrypted: " + decryptedData);
        } catch (Exception ignored) {
        }
    }
}

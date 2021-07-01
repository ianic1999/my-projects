package com.example.demo.util;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Converter
public class StringAttributeConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_NAME = "AES/ECB/PKCS5Padding";
    private static final String ENCRYPTION_KEY = "8WnUN6UNsA3yvCFg";

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null)
            return null;
        Key key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ALGORITHM);
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        Key key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), ALGORITHM);
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

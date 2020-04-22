package com.kingsoft.shiyou.uploader.s3.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author taoshuang on 2020/4/22.
 */
public final class StringUtils {
    private static final Charset UTF8;
    private static final MessageDigest MD5;

    static {
        UTF8 = Charset.forName("UTF-8");
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5(String s) {
        byte[] md5Bytes = MD5.digest(s.getBytes(UTF8));
        return hexEncode(md5Bytes);
    }

    public static String trim(String value, char ch) {
        if (value != null) {
            int len = value.length();
            if (len > 0 && value.charAt(0) == ch && value.charAt(len - 1) == ch) {
                value = value.substring(1, len - 1);
            }
        }
        return value;
    }

    /**
     * The byte[] returned by MessageDigest does not have a nice
     * textual representation, so some form of encoding is usually performed.
     * <p>
     * This implementation follows the example of David Flanagan's book
     * "Java In A Nutshell", and converts a byte array into a String
     * of hex characters.
     * <p>
     * Another popular alternative is to use a "Base64" encoding.
     */
    public static String hexEncode(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (byte b : bytes) {
            result.append(digits[(b & 0xf0) >> 4]);
            result.append(digits[b & 0x0f]);
        }
        return result.toString();
    }
}

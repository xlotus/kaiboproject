package com.tv.lib.core.algo;

import com.tv.lib.core.lang.StringUtils;

import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ShaUtil {

    /**
     * SHA加密数据
     * @param key 私钥
     * @param data 加密字符串
     * @return 返回加密结果
     */
    public static String HMACSHA256(String key, String data){
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            byte[] PRIVATE_KEY = StringUtils.stringToByte(key);
            SecretKeySpec secretKey = new SecretKeySpec(PRIVATE_KEY, "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] array = sha256HMAC.doFinal(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            String aesKey = sb.toString().toLowerCase();
            return aesKey.substring(25, 41);
        }catch (Exception exception){

        }
        return null;
    }

    public static byte[] SHA1(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = md.digest(input.getBytes("UTF-8"));
        return hashBytes;
    }

    public static String SHA1_Hex(String input) throws Exception {
        byte[] hashBytes = SHA1(input);
        return StringUtils.toHex(hashBytes);
    }
}

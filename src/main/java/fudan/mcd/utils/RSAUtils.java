package fudan.mcd.utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lwh on 2017/5/17.
 */

public class RSAUtils {
    public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIwvvnWMoCilonA4m5Mh8nUv3/tkQaOmnWa/E3BsP2IMv7DHtblNYALM5+dQq2axsaoynBbLxcV7L7MjmnIMxji9Asrmy6KmowlOYtQZ/tN2plINbzIR66DNblZKqYULBlFWcoNJct33MHv2DD2W+BiKgS8JqJkhzu/EYECIar45AgMBAAECgYBK9ey+JqgtlAQ8RcjK8EgZ6bO3VXDT9XANVPcsEnYCFBccjG3b3EgOt0r6IeTFURdTjyRv/B6Q7sIe5FbdzX7gXpJD+F/jrDQDd99z6LRuT8N9iGB02OejHkPcay3i7SkJ7PEMzczvKu/V7FEHniuHaS8PmMJW/3dd1tjvxXjKAQJBAPW+lIzuu3i+xkE6ZOsZr68C9UaReavr+tZdlzz3XfL5xbSO6k0SVysidayqPfs89axYSIghASTX8Nf+dpzwYJECQQCSCW520kQ3Y/yaEQ9AXcxK4g9X/UT3xpSnGLurwvBmehH5LYqtHl1OF8cj5pJq4XY6emtuUVVLQhynUFqwelcpAkEAxpSl+Cjmp5EKJiWo7W6JUFSEnezqtGnx/g4NK7/9VF5QTDL81SEP/ZRQISJFCePd5tJEcnSxNvOk9PX1Jab9cQJAen84zqivmfrf/2gB8A79925KSpdjC6fiN4vSmFArkPoiK27dHTwULhZZ765P8SFwJ7tadC37Kuao3FvVxqTIiQJAHiUh0DlJCqPuAPDlq4AOeZFewDqjY4WZ+eN3GvXMCZ86tL8RDfJELcoKMnOX5lSnNo6iKVglGEPt/NMl7WkfbQ==";
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCML751jKAopaJwOJuTIfJ1L9/7ZEGjpp1mvxNwbD9iDL+wx7W5TWACzOfnUKtmsbGqMpwWy8XFey+zI5pyDMY4vQLK5suipqMJTmLUGf7TdqZSDW8yEeugzW5WSqmFCwZRVnKDSXLd9zB79gw9lvgYioEvCaiZIc7vxGBAiGq+OQIDAQAB";

    /** RSA最大加密明文大小 */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** RSA最大解密密文大小 */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /** 加密算法RSA */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 生成公钥和私钥
     *
     * @throws Exception
     *
     */
    public static void getKeys() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        String publicKeyStr = getPublicKeyStr(publicKey);
        String privateKeyStr = getPrivateKeyStr(privateKey);

        System.out.println("公钥\r\n" + publicKeyStr);
        System.out.println("私钥\r\n" + privateKeyStr);
    }

    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus
     *            模
     * @param exponent
     *            公钥指数
     * @return
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus
     *            模
     * @param exponent
     *            指数
     * @return
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 公钥加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data) throws Exception {
        byte[] dataByte = data.getBytes();
        byte[] keyBytes = Base64Utils.decode(PUBLIC_KEY);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        // Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = dataByte.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(dataByte, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataByte, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64Utils.encode(encryptedData);
    }

    /**
     * 私钥解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data) throws Exception {
        byte[] encryptedData = Base64Utils.decode(data);
        byte[] keyBytes = Base64Utils.decode(PRIVATE_KEY);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        // Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher
                        .doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher
                        .doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
    }

    /**
     * 获取模数和密钥
     *
     * @return
     */
    public static Map<String, String> getModulusAndKeys() {

        Map<String, String> map = new HashMap<String, String>();

        try {
            InputStream in = RSAUtils.class
                    .getResourceAsStream("/rsa.properties");
            Properties prop = new Properties();
            prop.load(in);

            String modulus = prop.getProperty("modulus");
            String publicKey = prop.getProperty("publicKey");
            String privateKey = prop.getProperty("privateKey");

            in.close();

            map.put("modulus", modulus);
            map.put("publicKey", publicKey);
            map.put("privateKey", privateKey);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     *            公钥数据字符串
     * @throws Exception
     *             加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64Utils.decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从字符串中加载私钥<br>
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）。
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static PrivateKey loadPrivateKey(String privateKeyStr)
            throws Exception {
        try {
            byte[] buffer = Base64Utils.decode(privateKeyStr);
            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    public static String getPrivateKeyStr(PrivateKey privateKey)
            throws Exception {
        return new String(Base64Utils.encode(privateKey.getEncoded()));
    }

    public static String getPublicKeyStr(PublicKey publicKey) throws Exception {
        return new String(Base64Utils.encode(publicKey.getEncoded()));
    }

    public static void main(String[] args) throws Exception {
        getKeys();
    }
}

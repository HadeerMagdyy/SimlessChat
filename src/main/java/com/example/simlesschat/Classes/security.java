package com.example.simlesschat.Classes;
import android.util.Base64;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class security {

    public String decrypt(String outstring, String password) throws  Exception{
        SecretKeySpec key = generateKey(password);
        Cipher cipher =Cipher.getInstance("AES") ;
        cipher.init(Cipher.DECRYPT_MODE , key);
        byte []  decodeval  = Base64.decode(outstring   , Base64.DEFAULT) ;
        byte [] DEVal = cipher.doFinal(decodeval);
        String DVAL = new String(DEVal) ;
        return  DVAL ;
    }

    public String encrypt(String data, String password)  throws  Exception{

        SecretKeySpec key = generateKey(password);
        Cipher cipher =Cipher.getInstance("AES") ;
        cipher.init(Cipher.ENCRYPT_MODE , key);
        byte [] encVal = cipher.doFinal(data.getBytes());
        String ENCVAL = Base64.encodeToString(encVal  , Base64.DEFAULT) ;
        return  ENCVAL ;
    }

    public SecretKeySpec generateKey(String password) throws  Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256") ;
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES") ;
        return  secretKeySpec ;
    }
}

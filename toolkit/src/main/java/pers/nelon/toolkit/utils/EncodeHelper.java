package pers.nelon.toolkit.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by nelon on 17-9-8.
 */

public abstract class EncodeHelper {
    public static String toMD5(String pOriginal) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(pOriginal.getBytes());
            pOriginal = new String(digest, 0, digest.length);
        } catch (NoSuchAlgorithmException pE) {
            pE.printStackTrace();
        }
        return pOriginal;
    }
}

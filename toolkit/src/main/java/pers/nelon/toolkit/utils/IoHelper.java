package pers.nelon.toolkit.utils;

import android.support.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by nelon on 17-9-8.
 */

public abstract class IoHelper {
    public static boolean closeStream(@Nullable Closeable pCloseable) {
        boolean result = false;
        if (pCloseable != null) {
            try {
                pCloseable.close();
                result = true;
            } catch (IOException pE) {
                pE.printStackTrace();
            }
        }
        return result;
    }
}

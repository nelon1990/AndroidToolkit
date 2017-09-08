package pers.nelon.toolkit.cache;

import android.graphics.Bitmap;

import pers.nelon.toolkit.cache.impl.ICacheReader;
import pers.nelon.toolkit.cache.impl.ICacheWriter;

/**
 * Created by nelon on 17-8-25.
 */

public interface ICacheImpl {
    <T> boolean put(String pKey, ICacheWriter<T> pCacheWriter);

    <T> T get(String pKey, ICacheReader<T> pReader);

    boolean putBitmap(String pKey, Bitmap pBitmap);

    boolean putString(String pKey, String pString);

    Bitmap getBitmap(String pKey);

    String getString(String pKey);

    boolean hasCached(String pKey);

    void delete(String pKey);

    void clear();
}

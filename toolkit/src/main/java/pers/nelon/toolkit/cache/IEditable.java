package pers.nelon.toolkit.cache;

import android.graphics.Bitmap;

import pers.nelon.toolkit.cache.impl.ICacheWriter;

/**
 * Created by nelon on 17-9-25.
 */

public interface IEditable {
    boolean putString(String pKey, String pS);

    boolean putBitmap(String pKey, Bitmap pValue);

    void afterEachPut(String pKey);

    void afterCommit();

    void delete(String pKey);

    void clear();

    <T> boolean put(String pKey, ICacheWriter<T> pWriter);
}

package pers.nelon.toolkit.cache;

import android.graphics.Bitmap;

import pers.nelon.toolkit.cache.impl.ICacheReader;

/**
 * Created by nelon on 17-8-25.
 */

public interface ICacheImpl extends IEditable {

    <T> T get(String pKey, ICacheReader<T> pReader);

    Bitmap getBitmap(String pKey);

    String getString(String pKey);

    boolean has(String pKey);

    void close();

    void flush();
}

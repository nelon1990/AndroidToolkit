package pers.nelon.toolkit.cache.impl;

import android.graphics.Bitmap;

import java.io.InputStream;
import java.io.OutputStream;

import pers.nelon.toolkit.cache.ICacheImpl;
import pers.nelon.toolkit.cache.impl.reader.BitmapReader;
import pers.nelon.toolkit.cache.impl.writer.BitmapWriter;

/**
 * Created by nelon on 17-9-8.
 */

public abstract class BaseCacheImpl implements ICacheImpl {
    public BaseCacheImpl() {

    }

    @Override
    public <T> boolean put(String pKey, ICacheWriter<T> pCacheWriter) {
        return pCacheWriter.write(getCacheOutputStream(pKey));
    }

    protected abstract OutputStream getCacheOutputStream(String pKey);

    @Override
    public <T> T get(String pKey, ICacheReader<T> pReader) {
        return pReader.read(getCacheInputStream(pKey));
    }

    protected abstract InputStream getCacheInputStream(String pKey);


    @Override
    public boolean putBitmap(String pKey, Bitmap pBitmap) {
        return put(pKey, new BitmapWriter(pBitmap, Bitmap.CompressFormat.PNG, 100));
    }

    @Override
    public boolean putString(String pKey, String pString) {
        return false;
    }

    @Override
    public Bitmap getBitmap(String pKey) {
        return get(pKey, new BitmapReader(null, null));
    }

}

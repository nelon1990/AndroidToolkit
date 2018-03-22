package pers.nelon.toolkit.cache.impl;

import android.graphics.Bitmap;

import java.io.InputStream;
import java.io.OutputStream;

import pers.nelon.toolkit.cache.ICacheImpl;
import pers.nelon.toolkit.cache.impl.reader.BitmapReader;
import pers.nelon.toolkit.cache.impl.reader.StringReader;
import pers.nelon.toolkit.cache.impl.writer.BitmapWriter;
import pers.nelon.toolkit.cache.impl.writer.StringWriter;
import pers.nelon.toolkit.utils.IoHelper;

/**
 * Created by nelon on 17-9-8.
 */

public abstract class BaseCacheImpl implements ICacheImpl {
    public BaseCacheImpl() {

    }

    protected abstract OutputStream getCacheOutputStream(String pKey, long length);

    protected abstract InputStream getCacheInputStream(String pKey);


    @Override
    public void afterCommit() {

    }

    @Override
    public void afterEachPut(String pKey) {

    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }

    @Override
    public <T> boolean put(String pKey, ICacheWriter<T> pCacheWriter) {
        OutputStream outputStream = getCacheOutputStream(pKey, pCacheWriter.getLength());
        if (outputStream == null) {
            return false;
        }
        boolean write = pCacheWriter.write(outputStream);
        IoHelper.closeStream(outputStream);
        return write;
    }

    @Override
    public <T> T get(String pKey, ICacheReader<T> pReader) {
        InputStream inputStream = getCacheInputStream(pKey);
        if (inputStream == null) {
            return null;
        }
        T read = pReader.read(inputStream);
        IoHelper.closeStream(inputStream);
        return read;
    }

    @Override
    public String getString(String pKey) {
        return get(pKey, new StringReader());
    }

    @Override
    public boolean putString(String pKey, String pString) {
        return put(pKey, new StringWriter(pString));
    }

    @Override
    public boolean putBitmap(String pKey, Bitmap pBitmap) {
        return put(pKey, new BitmapWriter(pBitmap, Bitmap.CompressFormat.PNG, 100));
    }

    @Override
    public Bitmap getBitmap(String pKey) {
        return get(pKey, new BitmapReader(null, null));
    }

}

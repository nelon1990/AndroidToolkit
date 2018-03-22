package pers.nelon.toolkit.cache.impl;

import android.util.LruCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by nelon on 17-8-25.
 */

public class MemCacheImpl extends BaseCacheImpl {
    private final LruCache<String, XByteArrayOutputStream> mLruCache;

    /**
     * @param pMaxSize 单位为kByte
     */
    public MemCacheImpl(int pMaxSize) {
        mLruCache = new LruCache<String, XByteArrayOutputStream>(pMaxSize) {
            @Override
            protected int sizeOf(String key, XByteArrayOutputStream value) {
                return (int) value.mIdentifiedCapacity;
            }
        };
    }

    @Override
    protected OutputStream getCacheOutputStream(String pKey, long length) {
        XByteArrayOutputStream byteArrayOutputStream = mLruCache.get(pKey);
        if (byteArrayOutputStream == null) {
            byteArrayOutputStream = new XByteArrayOutputStream(length);
            mLruCache.put(pKey, byteArrayOutputStream);
        }
        byteArrayOutputStream.reset();
        return byteArrayOutputStream;
    }

    @Override
    protected InputStream getCacheInputStream(String pKey) {
        ByteArrayOutputStream byteArrayOutputStream = mLruCache.get(pKey);
        if (byteArrayOutputStream == null) {
            return new ByteArrayInputStream(new byte[]{});
        }
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }


    @Override
    public boolean has(String pKey) {
        return mLruCache.get(pKey) != null;
    }

    @Override
    public void delete(String pKey) {
        XByteArrayOutputStream remove = mLruCache.remove(pKey);
        try {
            remove.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        for (String key : mLruCache.snapshot().keySet()) {
            try {
                mLruCache.get(key).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mLruCache.evictAll();
    }

    @Override
    public void close() {
        clear();
    }

    private class XByteArrayOutputStream extends ByteArrayOutputStream {
        private long mIdentifiedCapacity;

        public XByteArrayOutputStream(long identifiedCapacity) {
            mIdentifiedCapacity = identifiedCapacity;
        }
    }
}

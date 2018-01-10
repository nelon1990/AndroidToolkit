package pers.nelon.toolkit.cache.impl;

import android.util.LruCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import pers.nelon.toolkit.utils.EncodeHelper;

/**
 * Created by nelon on 17-8-25.
 */

public class MemCacheImpl extends BaseCacheImpl {
    private final LruCache<String, ByteArrayOutputStream> mLruCache;

    /**
     * @param pMaxSize 单位为kByte
     */
    public MemCacheImpl(int pMaxSize) {
        mLruCache = new LruCache<String, ByteArrayOutputStream>(pMaxSize) {
            @Override
            protected int sizeOf(String key, ByteArrayOutputStream value) {
                return value.size();
            }
        };
    }

    @Override
    protected OutputStream getCacheOutputStream(String pKey) {
        ByteArrayOutputStream byteArrayOutputStream = mLruCache.get(pKey);
        if (byteArrayOutputStream == null) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            mLruCache.put(pKey, byteArrayOutputStream);
        }
        byteArrayOutputStream.reset();
        return byteArrayOutputStream;
    }

    @Override
    protected InputStream getCacheInputStream(String pKey) {
        ByteArrayOutputStream byteArrayOutputStream = mLruCache.get(pKey);
        if (byteArrayOutputStream == null) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            mLruCache.put(pKey, byteArrayOutputStream);
        }
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }


    @Override
    public boolean has(String pKey) {
        return mLruCache.get(pKey) != null;
    }

    @Override
    public void delete(String pKey) {
        mLruCache.remove(pKey);
    }

    @Override
    public void clear() {
        mLruCache.evictAll();
    }

    @Override
    public void close() {
        clear();
    }
}

package pers.nelon.toolkit.cache.impl;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import pers.nelon.toolkit.utils.EncodeHelper;

/**
 * Created by nelon on 17-8-25.
 */

public class MemCacheImpl extends BaseCacheImpl {
    private final LruCache<String, Object> mLruCache;
    private MessageDigest mMd5;

    /**
     * @param pMaxSize 单位为kByte
     */
    public MemCacheImpl(int pMaxSize) {
        mLruCache = new LruCache<String, Object>(pMaxSize) {
            @Override
            protected int sizeOf(String key, Object value) {
                if (value instanceof Bitmap) {
                    return ((Bitmap) value).getByteCount() / 1024;
                } else if (value instanceof String) {
                    return ((String) value).length() / 1024;
                }
                return super.sizeOf(key, value);
            }
        };
    }

    @Override
    protected OutputStream getCacheOutputStream(String pKey) {
        return null;
    }

    @Override
    protected InputStream getCacheInputStream(String pKey) {
        return null;
    }

    @Override
    public boolean putBitmap(String pKey, Bitmap pBitmap) {
        mLruCache.put(getEncodedKey(pKey), pBitmap);
        return true;
    }

    @Override
    public boolean putString(String pKey, String pString) {
        mLruCache.put(getEncodedKey(pKey), pString);
        return true;
    }

    @Override
    public Bitmap getBitmap(String pKey) {
        Object o = mLruCache.get(getEncodedKey(pKey));
        if (o != null && o instanceof Bitmap) {
            return (Bitmap) o;
        }
        return null;
    }

    @Override
    public String getString(String pKey) {
        Object o = mLruCache.get(getEncodedKey(pKey));
        if (o != null && o instanceof String) {
            return (String) o;
        }
        return null;
    }

    @Override
    public boolean hasCached(String pKey) {
        return mLruCache.get(getEncodedKey(pKey)) != null;
    }

    @Override
    public void delete(String pKey) {
        mLruCache.remove(getEncodedKey(pKey));
    }

    @Override
    public void clear() {
        mLruCache.evictAll();
    }

    private String getEncodedKey(String pKey) {
        return EncodeHelper.toMD5(pKey);
    }
}

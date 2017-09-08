package pers.nelon.toolkit.cache.impl;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pers.nelon.toolkit.utils.EncodeHelper;

/**
 * Created by nelon on 17-9-8.
 */

public class DiskCacheImpl extends BaseCacheImpl {

    private DiskLruCache mDiskLruCache;

    public DiskCacheImpl(File directory, int appVersion, int valueCount, long maxSize) {
        try {
            mDiskLruCache = DiskLruCache.open(directory, appVersion, valueCount, maxSize);
        } catch (IOException pE) {
            pE.printStackTrace();
            throw new UnknownError("can not create DiskCacheImpl :" + pE.getMessage());
        }
    }

    @Override
    public String getString(String pKey) {
        String string = "";
        try {
            string = mDiskLruCache.get(EncodeHelper.toMD5(pKey)).getString(0);
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        return string;
    }

    @Override
    public boolean hasCached(String pKey) {
        return false;
    }

    @Override
    public void delete(String pKey) {
        try {
            mDiskLruCache.remove(EncodeHelper.toMD5(pKey));
        } catch (IOException pE) {
            pE.printStackTrace();
        }
    }

    @Override
    public void clear() {
        try {
            mDiskLruCache.delete();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
    }

    @Override
    protected OutputStream getCacheOutputStream(String pKey) {
        OutputStream outputStream = null;
        try {
            outputStream = mDiskLruCache.edit(EncodeHelper.toMD5(pKey))
                    .newOutputStream(0);
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        return outputStream;
    }

    @Override
    protected InputStream getCacheInputStream(String pKey) {
        InputStream inputStream = null;
        try {
            inputStream = mDiskLruCache.get(EncodeHelper.toMD5(pKey)).getInputStream(0);
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        return inputStream;
    }

}

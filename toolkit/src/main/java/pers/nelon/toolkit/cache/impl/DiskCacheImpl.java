package pers.nelon.toolkit.cache.impl;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

import pers.nelon.toolkit.utils.EncodeHelper;

/**
 * Created by nelon on 17-9-8.
 */

public class DiskCacheImpl extends BaseCacheImpl {

    private DiskLruCache mDiskLruCache;
    private ConcurrentHashMap<String, DiskLruCache.Editor> mEditorMap;

    public DiskCacheImpl(File directory, int appVersion, int valueCount, long maxSize) {
        mEditorMap = new ConcurrentHashMap<>();
        try {
            mDiskLruCache = DiskLruCache.open(directory, appVersion, valueCount, maxSize);
        } catch (IOException pE) {
            pE.printStackTrace();
            throw new UnknownError("can not create DiskCacheImpl :" + pE.getMessage());
        }
    }

    @Override
    public boolean has(String pKey) {
        boolean result = false;
        try {
            result = mDiskLruCache.get(EncodeHelper.toMD5(pKey)).getInputStream(0) != null;
        } catch (Exception pE) {
            pE.printStackTrace();
        }
        return result;
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
    public void close() {
        try {
            mDiskLruCache.close();
            mEditorMap.clear();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
    }


    @Override
    public void flush() {
        try {
            if (!mDiskLruCache.isClosed()) {
                mDiskLruCache.flush();
            }
        } catch (IOException pE) {
            pE.printStackTrace();
        }
    }

    @Override
    protected OutputStream getCacheOutputStream(String pKey) {
        OutputStream outputStream = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(EncodeHelper.toMD5(pKey));
            mEditorMap.put(pKey, editor);
            outputStream = editor
                    .newOutputStream(0);
        } catch (Exception pE) {
            pE.printStackTrace();
        }
        return outputStream;
    }

    @Override
    protected InputStream getCacheInputStream(String pKey) {
        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(EncodeHelper.toMD5(pKey));
            inputStream = snapshot.getInputStream(0);
        } catch (Exception pE) {
            pE.printStackTrace();
        }
        return inputStream;
    }

    @Override
    public void afterEveryPut(String pKey) {
        DiskLruCache.Editor editor = mEditorMap.remove(pKey);
        if (editor != null) {
            try {
                editor.commit();
            } catch (Exception pE) {
                pE.printStackTrace();
            }
        }
    }
}

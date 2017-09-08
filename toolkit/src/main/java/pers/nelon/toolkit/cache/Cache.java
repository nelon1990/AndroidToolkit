package pers.nelon.toolkit.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pers.nelon.toolkit.cache.impl.DiskCacheImpl;
import pers.nelon.toolkit.cache.impl.MemCacheImpl;

/**
 * Created by nelon on 17-8-25.
 */

public class Cache {
    private static final String TAG = "Cache";

    private static ICacheWrapper sMemCacheWrapper;
    private static ICacheWrapper sDiskCacheWrapper;
    private static ICacheWrapper sCacheWrapper;
    private static int sMaxMemCacheSize = -1;
    private static File sDiskCacheTarget = null;
    private static int sMaxDiskCacheSize = -1;
    private static int sVersionCode;

    public static void init(Context pContext) {
        init(pContext, -1);
    }

    public static void init(Context pContext, int pMaxMemCacheSize) {
        init(pContext, pMaxMemCacheSize, null, -1);
    }

    public static void init(Context pContext, int pMaxMemCacheSize, File pDiskCacheTarget, int pMaxDiskCacheSize) {
        if (pMaxMemCacheSize < 0) {
            pMaxMemCacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
        }
        if (pDiskCacheTarget == null) {
            pDiskCacheTarget = pContext.getCacheDir();
        }

        if (pMaxDiskCacheSize < 0) {
            pMaxDiskCacheSize = 100 * 1024 * 1024;
        }
        try {
            @SuppressLint("WrongConstant") PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), 1);
            sVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException pE) {
            pE.printStackTrace();
        }
        sMaxMemCacheSize = pMaxMemCacheSize;
        sDiskCacheTarget = pDiskCacheTarget;
        sMaxDiskCacheSize = pMaxDiskCacheSize;
    }

    public static ICacheWrapper withMemDefault() {
        checkInit();
        if (sMemCacheWrapper == null) {
            synchronized (Cache.class) {
                if (sMemCacheWrapper == null) {
                    sMemCacheWrapper = new CacheWrapper(new MemCacheImpl(sMaxMemCacheSize));
                }
            }
        }
        return sMemCacheWrapper;
    }

    public static ICacheWrapper withDiskDefault() {
        checkInit();
        if (sDiskCacheWrapper == null) {
            synchronized (Cache.class) {
                if (sDiskCacheWrapper == null) {
                    sDiskCacheWrapper = new CacheWrapper(new DiskCacheImpl(sDiskCacheTarget, sVersionCode, 1, sMaxDiskCacheSize));
                }
            }
        }
        return sDiskCacheWrapper;
    }

    public static ICacheWrapper withDefault() {
        checkInit();
        if (sCacheWrapper == null) {
            synchronized (Cache.class) {
                if (sCacheWrapper == null) {
                    sCacheWrapper = new EJCacheWrapper(withMemDefault(), withDiskDefault());
                }
            }
        }
        return sCacheWrapper;
    }

    public static ICacheWrapper with(ICacheImpl pCache) {
        return new CacheWrapper(pCache);
    }

    private static void checkInit() {
        if (sDiskCacheTarget == null || sMaxDiskCacheSize < 0 || sMaxMemCacheSize < 0) {
            throw new RuntimeException("Cache may not initialize, you should call init() first");
        }
    }

    public interface ICacheWrapper {
        IEditor editor();

        boolean has(String pKey);

        Bitmap get(String pKey, Bitmap pDefault);

        int get(String pKey, int pDefault);

        String get(String pKey, String pDefault);

        float get(String pKey, float pDefault);

        long get(String pKey, long pDefault);

        double get(String pKey, double pDefault);

    }

    private static class CacheWrapper implements ICacheWrapper {

        private final ICacheImpl mCache;
        private final IEditor mEditor;

        CacheWrapper(ICacheImpl pCache) {
            mCache = pCache;
            mEditor = new Editor(pCache);
        }

        @Override
        public IEditor editor() {
            return mEditor;
        }

        @Override
        public boolean has(String pKey) {
            return mCache.hasCached(pKey);
        }

        @Override
        public Bitmap get(String pKey, Bitmap pDefault) {
            Bitmap bitmap = mCache.getBitmap(pKey);
            return bitmap == null ? pDefault : bitmap;
        }

        @Override
        public String get(String pKey, String pDefault) {
            String string = mCache.getString(pKey);
            if (TextUtils.isEmpty(string)) {
                string = pDefault;
            }
            return string;
        }

        @Override
        public final int get(String pKey, int pDefault) {
            int result = pDefault;
            try {
                result = Integer.valueOf(get(pKey, String.valueOf(pDefault)));
            } catch (NumberFormatException pE) {
                pE.printStackTrace();
            }
            return result;
        }

        @Override
        public final float get(String pKey, float pDefault) {
            float result = pDefault;
            try {
                result = Float.valueOf(get(pKey, String.valueOf(pDefault)));
            } catch (NumberFormatException pE) {
                pE.printStackTrace();
            }
            return result;
        }

        @Override
        public final long get(String pKey, long pDefault) {
            long result = pDefault;
            try {
                result = Long.valueOf(get(pKey, String.valueOf(pDefault)));
            } catch (NumberFormatException pE) {
                pE.printStackTrace();
            }
            return result;
        }

        @Override
        public final double get(String pKey, double pDefault) {
            double result = pDefault;
            try {
                result = Double.valueOf(get(pKey, String.valueOf(pDefault)));
            } catch (NumberFormatException pE) {
                pE.printStackTrace();
            }
            return result;
        }


    }

    private static class EJCacheWrapper implements ICacheWrapper {
        private final IEditor mEjEditor;
        private ICacheWrapper mDiskCacheWrapper;
        private ICacheWrapper mMemCacheWrapper;

        private EJCacheWrapper(ICacheWrapper pDiskCacheWrapper, ICacheWrapper pMemCacheWrapper) {
            mDiskCacheWrapper = pDiskCacheWrapper;
            mMemCacheWrapper = pMemCacheWrapper;
            mEjEditor = new EJEditor(mMemCacheWrapper.editor(), mDiskCacheWrapper.editor());
        }

        @Override
        public IEditor editor() {
            return mEjEditor;
        }

        @Override
        public boolean has(String pKey) {
            return mMemCacheWrapper.has(pKey) || mDiskCacheWrapper.has(pKey);
        }

        @Override
        public Bitmap get(String pKey, Bitmap pDefault) {
            return mMemCacheWrapper.get(pKey, mDiskCacheWrapper.get(pKey, pDefault));
        }

        @Override
        public int get(String pKey, int pDefault) {
            return mMemCacheWrapper.get(pKey, mDiskCacheWrapper.get(pKey, pDefault));
        }

        @Override
        public String get(String pKey, String pDefault) {
            return mMemCacheWrapper.get(pKey, mDiskCacheWrapper.get(pKey, pDefault));
        }

        @Override
        public float get(String pKey, float pDefault) {
            return mMemCacheWrapper.get(pKey, mDiskCacheWrapper.get(pKey, pDefault));
        }

        @Override
        public long get(String pKey, long pDefault) {
            return mMemCacheWrapper.get(pKey, mDiskCacheWrapper.get(pKey, pDefault));
        }

        @Override
        public double get(String pKey, double pDefault) {
            return mMemCacheWrapper.get(pKey, mDiskCacheWrapper.get(pKey, pDefault));
        }
    }

    private static class Editor implements IEditor, Runnable {
        private static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();
        private final ICacheImpl mCache;
        private final ThreadLocal<Queue<IEditorOperation>> mThreadLocal = new ThreadLocal<Queue<IEditorOperation>>() {
            @Override
            protected Queue<IEditorOperation> initialValue() {
                return new LinkedList<>();
            }
        };

        private Editor(ICacheImpl pCache) {
            mCache = pCache;
        }

        @Override
        public Editor put(String pKey, Bitmap pBitmap) {
            mThreadLocal.get().add(new EditorPut(mCache, pKey, pBitmap));
            return this;
        }

        @Override
        public Editor put(String pKey, String pString) {
            mThreadLocal.get().add(new EditorPut(mCache, pKey, pString));
            return this;
        }

        @Override
        public Editor put(String pKey, int pInt) {
            put(pKey, String.valueOf(pInt));
            return this;
        }

        @Override
        public Editor put(String pKey, float pFloat) {
            put(pKey, String.valueOf(pFloat));
            return this;
        }

        @Override
        public Editor put(String pKey, long pLong) {
            put(pKey, String.valueOf(pLong));
            return this;
        }

        @Override
        public Editor put(String pKey, double pDouble) {
            put(pKey, String.valueOf(pDouble));
            return this;
        }

        @Override
        public IEditor delete(String pKey) {
            mThreadLocal.get().add(new EditorDelete(mCache, pKey));
            return this;
        }

        @Override
        public IEditor clear() {
            mThreadLocal.get().add(new EditorClear(mCache));
            return this;
        }

        @Override
        public void run() {
            IEditorOperation operation;
            do {
                Queue<IEditorOperation> iEditorOperations = mThreadLocal.get();
                operation = iEditorOperations.poll();
                if (operation != null) {
                    operation.opt();
                }
            } while (operation != null);
        }

        @Override
        public void commit() {
            run();
        }

        @Override
        public void commitAsync() {
            sExecutorService.submit(this);
        }


        private class EditorPut implements IEditorOperation {
            private final String mKey;
            private final Object mValue;
            private final ICacheImpl mCache;

            private EditorPut(ICacheImpl pCache, String pKey, Object pValue) {
                mCache = pCache;
                mKey = pKey;
                mValue = pValue;
            }

            @Override
            public void opt() {
                if (mValue instanceof String) {
                    mCache.putString(mKey, String.valueOf(mValue));
                } else if (mValue instanceof Bitmap) {
                    mCache.putBitmap(mKey, (Bitmap) mValue);
                }
            }
        }

        private class EditorDelete implements IEditorOperation {
            private final String mKey;
            private final ICacheImpl mCache;

            private EditorDelete(ICacheImpl pCache, String pKey) {
                mCache = pCache;
                mKey = pKey;
            }

            @Override
            public void opt() {
                mCache.delete(mKey);
            }
        }


        private class EditorClear implements IEditorOperation {
            private final ICacheImpl mCache;

            private EditorClear(ICacheImpl pCache) {
                mCache = pCache;
            }

            @Override
            public void opt() {
                mCache.clear();
            }
        }
    }

    private static class EJEditor implements IEditor {
        private IEditor mMemCacheEditor;
        private IEditor mDiskCacheEditor;

        private EJEditor(IEditor pMemCacheEditor, IEditor pDiskCacheEditor) {
            mMemCacheEditor = pMemCacheEditor;
            mDiskCacheEditor = pDiskCacheEditor;
        }

        @Override
        public IEditor put(String pKey, Bitmap pBitmap) {
            mMemCacheEditor.put(pKey, pBitmap);
            mDiskCacheEditor.put(pKey, pBitmap);
            return this;
        }

        @Override
        public IEditor put(String pKey, int pInt) {
            mMemCacheEditor.put(pKey, pInt);
            mDiskCacheEditor.put(pKey, pInt);
            return this;
        }

        @Override
        public IEditor put(String pKey, String pString) {
            mMemCacheEditor.put(pKey, pString);
            mDiskCacheEditor.put(pKey, pString);
            return this;
        }

        @Override
        public IEditor put(String pKey, float pFloat) {
            mMemCacheEditor.put(pKey, pFloat);
            mDiskCacheEditor.put(pKey, pFloat);
            return this;
        }

        @Override
        public IEditor put(String pKey, long pLong) {
            mMemCacheEditor.put(pKey, pLong);
            mDiskCacheEditor.put(pKey, pLong);
            return this;
        }

        @Override
        public IEditor put(String pKey, double pDouble) {
            mMemCacheEditor.put(pKey, pDouble);
            mDiskCacheEditor.put(pKey, pDouble);
            return this;
        }

        @Override
        public IEditor delete(String pKey) {
            mMemCacheEditor.delete(pKey);
            mDiskCacheEditor.delete(pKey);
            return this;
        }

        @Override
        public IEditor clear() {
            mMemCacheEditor.clear();
            mDiskCacheEditor.clear();
            return this;
        }

        @Override
        public void commit() {
            mMemCacheEditor.commit();
            mDiskCacheEditor.commit();
        }

        @Override
        public void commitAsync() {
            mMemCacheEditor.commitAsync();
            mDiskCacheEditor.commitAsync();
        }

    }

}


package pers.nelon.toolkit.cache;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
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
import pers.nelon.toolkit.cache.impl.ICacheReader;
import pers.nelon.toolkit.cache.impl.ICacheWriter;
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

    public static void init(Activity pActivity) {
        FragmentManager fragmentManager = pActivity.getFragmentManager();
        fragmentManager.beginTransaction()
                .add(new LifeCircleFragment(), "LifeCircleMonitor")
                .commit();

        init((Context) pActivity);
    }

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
            @SuppressLint("WrongConstant") PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            sVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException pE) {
            pE.printStackTrace();
        }
        sMaxMemCacheSize = pMaxMemCacheSize;
        sDiskCacheTarget = pDiskCacheTarget;
        sMaxDiskCacheSize = pMaxDiskCacheSize;
    }

    public static ICacheWrapper withMemCache() {
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

    public static ICacheWrapper withDiskCache() {
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

    public static ICacheWrapper withL2Cache() {
        checkInit();
        if (sCacheWrapper == null) {
            synchronized (Cache.class) {
                if (sCacheWrapper == null) {
                    sCacheWrapper = new L2CacheWrapper(withMemCache(), withDiskCache());
                }
            }
        }
        return sCacheWrapper;
    }

    public static ICacheWrapper with(ICacheImpl pCache) {
        return new CacheWrapper(pCache);
    }

    public static void release() {
        if (sMemCacheWrapper != null) {
            sMemCacheWrapper.close();
            sMemCacheWrapper = null;
        }
        if (sDiskCacheWrapper != null) {
            sDiskCacheWrapper.close();
            sDiskCacheWrapper = null;
        }
        if (sCacheWrapper != null) {
            sCacheWrapper.close();
            sCacheWrapper = null;
        }
    }

    public static void flush() {
        if (sMemCacheWrapper != null) {
            sMemCacheWrapper.flush();
        }
        if (sDiskCacheWrapper != null) {
            sDiskCacheWrapper.flush();
        }
        if (sCacheWrapper != null) {
            sCacheWrapper.flush();
        }
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

        <T> T get(String pKey, ICacheReader<T> pReader, T pDefault);

        void close();

        void flush();
    }

    private static class CacheWrapper implements ICacheWrapper {

        private final ICacheImpl mImpl;
        private final IEditor mEditor;

        CacheWrapper(ICacheImpl pImpl) {
            mImpl = pImpl;
            mEditor = new Editor(pImpl);
        }

        @Override
        public IEditor editor() {
            return mEditor;
        }

        @Override
        public boolean has(String pKey) {
            return mImpl.has(pKey);
        }

        @Override
        public Bitmap get(String pKey, Bitmap pDefault) {
            Bitmap bitmap = mImpl.getBitmap(pKey);
            return bitmap == null ? pDefault : bitmap;
        }

        @Override
        public String get(String pKey, String pDefault) {
            String string = mImpl.getString(pKey);
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

        @Override
        public <T> T get(String pKey, ICacheReader<T> pReader, T pDefault) {
            T result = pDefault;
            try {
                result = mImpl.get(pKey, pReader);
            } catch (NumberFormatException pE) {
                pE.printStackTrace();
            }
            return result;
        }

        @Override
        public void close() {
            mImpl.close();
        }

        @Override
        public void flush() {
            mImpl.flush();
        }


    }

    private static class L2CacheWrapper implements ICacheWrapper {
        private final IEditor mEjEditor;
        private ICacheWrapper mDiskCacheWrapper;
        private ICacheWrapper mMemCacheWrapper;

        private L2CacheWrapper(ICacheWrapper pDiskCacheWrapper, ICacheWrapper pMemCacheWrapper) {
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
            Bitmap result = mMemCacheWrapper.get(pKey, pDefault);
            if (result == null || result.equals(pDefault)) {
                result = mDiskCacheWrapper.get(pKey, pDefault);
            }
            return result;
        }

        @Override
        public int get(String pKey, int pDefault) {
            int result = mMemCacheWrapper.get(pKey, pDefault);
            if (result == pDefault) {
                result = mDiskCacheWrapper.get(pKey, pDefault);
            }
            return result;
        }

        @Override
        public String get(String pKey, String pDefault) {
            String result = mMemCacheWrapper.get(pKey, pDefault);
            if (result == null || result.equals(pDefault)) {
                result = mDiskCacheWrapper.get(pKey, pDefault);
            }
            return result;
        }

        @Override
        public float get(String pKey, float pDefault) {
            float result = mMemCacheWrapper.get(pKey, pDefault);
            if (result == pDefault) {
                result = mDiskCacheWrapper.get(pKey, pDefault);
            }
            return result;
        }

        @Override
        public long get(String pKey, long pDefault) {
            long result = mMemCacheWrapper.get(pKey, pDefault);
            if (result == pDefault) {
                result = mDiskCacheWrapper.get(pKey, pDefault);
            }
            return result;
        }

        @Override
        public double get(String pKey, double pDefault) {
            double result = mMemCacheWrapper.get(pKey, pDefault);
            if (result == pDefault) {
                result = mDiskCacheWrapper.get(pKey, pDefault);
            }
            return result;
        }

        @Override
        public <T> T get(String pKey, ICacheReader<T> pReader, T pDefault) {
            T result = mMemCacheWrapper.get(pKey, pReader, pDefault);
            if (result == pDefault) {
                result = mDiskCacheWrapper.get(pKey, pReader, pDefault);
            }
            return result;
        }


        @Override
        public void close() {
            mDiskCacheWrapper.close();
            mMemCacheWrapper.close();
        }

        @Override
        public void flush() {
            mMemCacheWrapper.flush();
            mDiskCacheWrapper.flush();
        }
    }

    private static class Editor implements IEditor, Runnable {
        private static ExecutorService sExecutorService;
        private final IEditable mEditable;
        private final ThreadLocal<Queue<IEditorOpt>> mThreadLocal = new ThreadLocal<Queue<IEditorOpt>>() {
            @Override
            protected Queue<IEditorOpt> initialValue() {
                return new LinkedList<>();
            }
        };

        private Editor(IEditable pEditable) {
            mEditable = pEditable;
        }

        private ExecutorService getExecutorService() {
            if (sExecutorService == null || !sExecutorService.isShutdown()) {
                synchronized (Editor.class) {
                    if (sExecutorService == null || !sExecutorService.isShutdown()) {
                        sExecutorService = Executors.newSingleThreadExecutor();
                    }
                }
            }

            return sExecutorService;
        }

        @Override
        public Editor put(String pKey, Bitmap pBitmap) {
            mThreadLocal.get().add(new EditorPut(mEditable, pKey, pBitmap));
            return this;
        }

        @Override
        public Editor put(String pKey, String pString) {
            mThreadLocal.get().add(new EditorPut(mEditable, pKey, pString));
            return this;
        }

        @Override
        public Editor put(String pKey, int pInt) {
            return put(pKey, String.valueOf(pInt));
        }

        @Override
        public Editor put(String pKey, float pFloat) {
            return put(pKey, String.valueOf(pFloat));
        }

        @Override
        public Editor put(String pKey, long pLong) {
            return put(pKey, String.valueOf(pLong));
        }

        @Override
        public Editor put(String pKey, double pDouble) {
            return put(pKey, String.valueOf(pDouble));
        }

        @Override
        public IEditor put(String pKey, ICacheWriter pWriter) {
            mThreadLocal.get().add(new EditorPut(mEditable, pKey, pWriter));
            return this;
        }

        @Override
        public IEditor delete(String pKey) {
            mThreadLocal.get().add(new EditorDelete(mEditable, pKey));
            return this;
        }

        @Override
        public IEditor clear() {
            mThreadLocal.get().add(new EditorClear(mEditable));
            return this;
        }

        @Override
        public void run() {
            try {
                IEditorOpt operation;
                do {
                    Queue<IEditorOpt> iEditorOpts = mThreadLocal.get();
                    operation = iEditorOpts.poll();
                    if (operation != null) {
                        operation.opt();
                    }
                } while (operation != null);
            } finally {
                mEditable.afterCommit();
            }
        }

        @Override
        public void commit() {
            run();
        }

        @Override
        public ICommitFuture commitAsync() {
            return new CommitFuture(getExecutorService().submit(this));
        }


        private class EditorPut implements IEditorOpt {
            private final String mKey;
            private final Object mValue;
            private final IEditable mEditable;

            private EditorPut(IEditable pEditable, String pKey, Object pValue) {
                mEditable = pEditable;
                mKey = pKey;
                mValue = pValue;
            }

            @Override
            public void opt() {
                if (mValue instanceof String) {
                    mEditable.putString(mKey, String.valueOf(mValue));
                    mEditable.afterEveryPut(mKey);
                } else if (mValue instanceof Bitmap) {
                    mEditable.putBitmap(mKey, (Bitmap) mValue);
                    mEditable.afterEveryPut(mKey);
                } else if (mValue instanceof ICacheWriter) {
                    mEditable.put(mKey, (ICacheWriter<Object>) mValue);
                    mEditable.afterEveryPut(mKey);
                }
            }
        }

        private class EditorDelete implements IEditorOpt {
            private final String mKey;
            private final IEditable mEditable;

            private EditorDelete(IEditable pEditable, String pKey) {
                mEditable = pEditable;
                mKey = pKey;
            }

            @Override
            public void opt() {
                mEditable.delete(mKey);
            }
        }


        private class EditorClear implements IEditorOpt {
            private final IEditable mEditable;

            private EditorClear(IEditable pEditable) {
                mEditable = pEditable;
            }

            @Override
            public void opt() {
                mEditable.clear();
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
        public IEditor put(String pKey, ICacheWriter pWriter) {
            mDiskCacheEditor.put(pKey, pWriter);
            mMemCacheEditor.put(pKey, pWriter);
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
        public ICommitFuture commitAsync() {
            return new FutureWrapper(mMemCacheEditor.commitAsync(), mDiskCacheEditor.commitAsync());
        }

    }

}


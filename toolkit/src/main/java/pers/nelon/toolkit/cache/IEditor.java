package pers.nelon.toolkit.cache;

import android.graphics.Bitmap;

import pers.nelon.toolkit.cache.impl.ICacheWriter;

/**
 * Created by nelon on 17-8-28.
 */

public interface IEditor {

    IEditor put(String pKey, Bitmap pBitmap);

    IEditor put(String pKey, int pInt);

    IEditor put(String pKey, String pString);

    IEditor put(String pKey, float pFloat);

    IEditor put(String pKey, long pLong);

    IEditor put(String pKey, double pDouble);

    IEditor put(String pKey, ICacheWriter pWriter);

    IEditor delete(String pKey);

    IEditor clear();

    void commit();

    ICommitFuture commitAsync();

    ICommitFuture commitAsync(CommitListener listener);

    interface CommitListener {
        void onCommitCompleted();
    }

    interface IEditorOpt {
        void opt();
    }
}

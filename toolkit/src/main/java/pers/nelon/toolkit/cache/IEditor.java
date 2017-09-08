package pers.nelon.toolkit.cache;

import android.graphics.Bitmap;

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

    IEditor delete(String pKey);

    IEditor clear();

    void commit();

    void commitAsync();

    interface IEditorOperation {
        void opt();
    }
}

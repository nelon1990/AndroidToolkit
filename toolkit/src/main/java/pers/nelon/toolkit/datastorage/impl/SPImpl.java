package pers.nelon.toolkit.datastorage.impl;

import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

import pers.nelon.toolkit.datastorage.IDataStorage;

/**
 * Created by nelon on 17-9-8.
 */

public class SPImpl implements IDataStorage.IStorageSimpleImpl {

    private final WeakReference<SharedPreferences> mReference;

    public SPImpl(SharedPreferences pSharedPreferences) {
        mReference = new WeakReference<>(pSharedPreferences);
    }

    @Override
    public String getString(String pKey, String pDefValue) {
        return mReference.get().getString(pKey, pDefValue);
    }

    @Override
    public void putString(String pKey, String pVal) {
        mReference.get()
                .edit()
                .putString(pKey, pVal)
                .apply();
    }

    @Override
    public void release() {
        mReference.clear();
    }

}

package pers.nelon.toolkit.datastorage;

import android.support.annotation.IntDef;
import android.support.annotation.UiThread;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by nelon on 17-5-26.
 */

public interface IDataStorage {

    void putByte(String pKey, byte pVal);

    byte getByte(String pKey, byte pDefValue);

    void putFloat(String pKey, float pVal);

    float getFloat(String pKey, float pDefValue);

    void putDouble(String pKey, double pVal);

    double getDouble(String pKey, double pDefValue);

    void putInt(String pKey, int pVal);

    int getInt(String pKey, int pDefValue);

    void putLong(String pKey, long pVal);

    long getLong(String pKey, long pDefValue);

    void putString(String pKey, String pVal);

    String getString(String pKey, String pDefValue);

    void registerObserver(DataObserver pDataObserver);

    void unregisterObserver(DataObserver pDataObserver);

    void registerDataStateListener(DataStateListener pListener);

    void unregisterDataStateListener(DataStateListener pListener);

    interface IStorageSimpleImpl {
        String getString(String pKey, String pDefValue);

        void putString(String pKey, String pVal);

        void release();
    }


    @IDataStorage.DataStateListener.DataStateCode
    int getDataState(String pKey);

    void release();


    interface DataStateListener {
        int STATUS_OK = 1;  //数据已经获取
        int STATUS_NONE = 2;  //未获取
        int STATUS_FAIL = 3;  //获取失败
        int STATUS_OBTAINING = 4;  //正在获取

        void onStateChanged(String pKey, @DataStateCode int pNewState, @DataStateCode int pOldState);

        @IntDef({STATUS_OK, STATUS_NONE, STATUS_FAIL, STATUS_OBTAINING})
        @Retention(RetentionPolicy.SOURCE)
        @interface DataStateCode {

        }
    }

    interface DataObserver {
        @UiThread
        void onChange(String pKey, String pNew, String pOld);
    }
}

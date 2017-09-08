package pers.nelon.toolkit.datastorage;

import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pers.nelon.toolkit.datastorage.IDataStorage.DataStateListener.STATUS_FAIL;
import static pers.nelon.toolkit.datastorage.IDataStorage.DataStateListener.STATUS_NONE;
import static pers.nelon.toolkit.datastorage.IDataStorage.DataStateListener.STATUS_OBTAINING;
import static pers.nelon.toolkit.datastorage.IDataStorage.DataStateListener.STATUS_OK;

/**
 * Created by nelon on 17-7-25.
 * 状态转换规则:
 * ________OK
 * _____↙↗  ↑  ↖
 * ___NONE  →  OBTAINING
 * ______↘  ↑  ↙
 * ________FAIL
 */
public class DataStorage implements IDataStorage {
    private static final String TAG = "DataStorage";

    private Set<DataObserver> mObservers = new HashSet<>();
    private Handler mHandler = new Handler();
    private IStorageSimpleImpl mStorage;
    private Map<String, Integer> mStateMap = new ArrayMap<>();
    private List<DataStateListener> mDataStateListeners = new ArrayList<>();

    private DataStorage(IStorageSimpleImpl pStorage) {
        mStorage = pStorage;
    }

    public static IDataStorage wrap(IStorageSimpleImpl pStorage) {
        if (pStorage == null) {
            throw new NullPointerException("IDataStorageImpl could not be null");
        }
        return new DataStorage(pStorage);
    }

    @Override
    public final void putByte(String pKey, byte pVal) {
        putString(pKey, String.valueOf(pVal));
    }

    @Override
    public final byte getByte(String pKey, byte pDefValue) {
        return Byte.valueOf(getString(pKey, String.valueOf(pDefValue)));
    }

    @Override
    public final void putFloat(String pKey, float pVal) {
        putString(pKey, String.valueOf(pVal));
    }

    @Override
    public final float getFloat(String pKey, float pDefValue) {
        return Float.valueOf(getString(pKey, String.valueOf(pDefValue)));
    }

    @Override
    public final void putDouble(String pKey, double pVal) {
        putString(pKey, String.valueOf(pVal));
    }

    @Override
    public final double getDouble(String pKey, double pDefValue) {
        return Double.valueOf(getString(pKey, String.valueOf(pDefValue)));
    }

    @Override
    public final void putInt(String pKey, int pVal) {
        putString(pKey, String.valueOf(pVal));
    }

    @Override
    public final int getInt(String pKey, int pDefValue) {
        return Integer.valueOf(getString(pKey, String.valueOf(pDefValue)));
    }

    @Override
    public final void putLong(String pKey, long pVal) {
        putString(pKey, String.valueOf(pVal));
    }

    @Override
    public final long getLong(String pKey, long pDefValue) {
        return Long.valueOf(getString(pKey, String.valueOf(pDefValue)));
    }

    @Override
    public final String getString(String pKey, String pDefValue) {
        String result = mStorage.getString(pKey, pDefValue);
        if (!result.equals(pDefValue)) {
            if (getDataState(pKey) == STATUS_NONE) {
                updateState(pKey, STATUS_OK);
            }
        }
        return result;
    }


    @Override
    public final void putString(String pKey, String pVal) {
        updateState(pKey, TextUtils.isEmpty(pVal) ? STATUS_NONE : STATUS_OK);
        String oldVal = mStorage.getString(pKey, "");
        mStorage.putString(pKey, pVal);
        String newVal = mStorage.getString(pKey, oldVal);
        if (!newVal.equals(oldVal)) {
            notifyDataChanged(pKey, newVal, oldVal);
        }
    }

    private synchronized void updateState(String pKey, @DataStateListener.DataStateCode int pState) {
        int oldState = getDataState(pKey);
        StateNext stateByCode = StateNext.getStateByCode(oldState);
        if (stateByCode.canGoNext(pState) && pState != oldState) {
            mStateMap.put(pKey, pState);
            for (IDataStorage.DataStateListener listener : mDataStateListeners) {
                listener.onStateChanged(pKey, pState, oldState);
            }
        }

    }

    @Override
    public final void release() {
        mStorage.release();
        mStorage = null;
        mObservers.clear();
        mHandler = null;
    }


    @Override
    public final void registerObserver(DataObserver pDataObserver) {
        mObservers.add(pDataObserver);
    }

    @Override
    public final void unregisterObserver(DataObserver pDataObserver) {
        mObservers.remove(pDataObserver);
    }

    @Override
    public void registerDataStateListener(IDataStorage.DataStateListener pListener) {
        if (!mDataStateListeners.contains(pListener)) {
            mDataStateListeners.add(pListener);
        }
    }

    @Override
    public void unregisterDataStateListener(IDataStorage.DataStateListener pListener) {
        if (mDataStateListeners.contains(pListener)) {
            mDataStateListeners.remove(pListener);
        }
    }

    @Override
    public int getDataState(String pKey) {
        Integer state = mStateMap.get(pKey);
        if (state == null) {
            state = STATUS_NONE;
            mStateMap.put(pKey, STATUS_NONE);
        }
        return state;
    }

    private void notifyDataChanged(final String pKey, final String pNew, final String pOld) {
        for (final DataObserver mObserver : mObservers) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mObserver.onChange(pKey, pNew, pOld);
                }
            });
        }
    }

    private enum StateNext {
        OK(STATUS_NONE),
        NONE(STATUS_OK, STATUS_OBTAINING, STATUS_FAIL),
        FAIL(STATUS_OK),
        OBTAINING(STATUS_OK, STATUS_FAIL);

        private int[] nextAvailableStateCode;

        StateNext(@IDataStorage.DataStateListener.DataStateCode int... pNextAvailableStateCode) {
            nextAvailableStateCode = pNextAvailableStateCode;
        }

        static StateNext getStateByCode(@IDataStorage.DataStateListener.DataStateCode int pStateCode) {
            switch (pStateCode) {
                case STATUS_FAIL:
                    return FAIL;
                case STATUS_OBTAINING:
                    return OBTAINING;
                case STATUS_OK:
                    return OK;
                case STATUS_NONE:
                default:
                    return NONE;
            }
        }

        boolean canGoNext(@IDataStorage.DataStateListener.DataStateCode int pState) {
            boolean can = false;
            for (int state : nextAvailableStateCode) {
                if (state == pState) {
                    can = true;
                    break;
                }
            }
            return can;
        }
    }
}

package pers.nelon.mvp;

import android.content.Context;

/**
 * Created by nelon on 2017/12/20.
 */

public abstract class BaseModel implements IBaseContract.IModel {
    private Context mContext;

    public BaseModel(Context Context) {
        mContext = Context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public final void release() {
        innerRelease();
        mContext = null;
    }

    abstract void innerRelease();
}

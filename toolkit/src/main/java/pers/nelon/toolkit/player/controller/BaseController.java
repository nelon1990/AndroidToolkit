package pers.nelon.toolkit.player.controller;

import android.content.Context;
import android.support.annotation.CallSuper;

/**
 * Created by nelon on 17-7-4.
 */

public abstract class BaseController implements IController {

    private Context mContext;

    public BaseController(Context pContext) {
        mContext = pContext;
    }

    public Context getContext() {
        return mContext;
    }

    @CallSuper
    @Override
    public final void release() {
        releaseInner();
        mContext = null;
    }

    protected abstract void releaseInner();
}

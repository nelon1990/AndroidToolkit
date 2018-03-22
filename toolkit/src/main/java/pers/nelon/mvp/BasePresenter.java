package pers.nelon.mvp;

import android.content.Context;

/**
 * 组件业务实现代理类的基类，定义了Presenter类的构造方式及业务无关的基础方法
 * <p>
 * Created by nelon on 2017/12/20.
 */

public abstract class BasePresenter<V extends IBaseContract.IView, M extends IBaseContract.IModel> implements IBaseContract.IPresenter {
    private V mView;
    private M mModel;
    private Context mContext;

    public BasePresenter(Context context, V view, M model) {
        mView = view;
        mModel = model;
        mContext = context;
    }

    @Override
    public void release() {
        innerRelease();
        mView = null;
        mModel.release();
        mModel = null;
        mContext = null;
    }

    abstract void innerRelease();

    public V getView() {
        return mView;
    }

    public M  getModel() {
        return mModel;
    }

    public Context getContext() {
        return mContext;
    }
}

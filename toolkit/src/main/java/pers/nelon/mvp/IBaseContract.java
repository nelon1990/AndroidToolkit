package pers.nelon.mvp;

import android.content.Intent;

/**
 * 组件契约接口定义
 * <p>
 * Created by nelon on 2017/12/20.
 */

public interface IBaseContract {
    /**
     * 定义组件的的UI相关的接口，被 Presenter 持有
     */
    interface IView {

        void showError(String message);

        void showLoading(boolean showLoading);

        void showDialog(String message, OnConfirmListener onConfirmListener);

        interface OnConfirmListener {
            void onConfirm();
        }
    }

    /**
     * 定义组件业务相关的接口，被 IView 持有
     */
    interface IPresenter {
        /**
         * View 在需要释放资源时，调用方法
         * Presenter 在此方法中执行资源的释放操作
         */
        void release();
    }

    /**
     * 定义组件数据相关的接口，被 Presenter 持有
     */
    interface IModel {
        /**
         * Presenter 在需要释放资源时，调用方法
         * Model 在此方法中执行资源的释放操作
         */
        void release();
    }
}

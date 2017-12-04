package pers.nelon.toolkit;

import android.support.annotation.Nullable;
import android.support.v4.util.Pools;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可根據Tag進行對象管理的對象池，實現對對象的精準管理
 * Created by 李冰锋 on 2017/3/2 9:42.
 */
public class TaggedPool<O> implements Pools.Pool<TaggedPool.Holder> {
    public final static String TAG = TaggedPool.class.getSimpleName();

    private List<String> mLockedTagList = new ArrayList<>(); //指定tag的holder只能被相同tag的请求使用

    private Map<String, Holder<O>> mTagHolderMap = new ConcurrentHashMap<>();
    private Map<String, Holder<O>> mEmptyTagHolderMap = new ConcurrentHashMap<>();
    private final Class<? extends Holder<O>> mHolderCreator;

    public static synchronized <V> TaggedPool<V> newInstance(Class<? extends Holder<V>> holderType) {
        return new TaggedPool<>(holderType);
    }

    private TaggedPool(Class<? extends Holder<O>> holderCreator) {
        mHolderCreator = holderCreator;
    }

    /**
     * 根据tag申请holder，若tag被占用，则先释放被占用tag的holder，再返回可用的holder
     * @param pTag 需申请的tag
     * @return  可用的holder
     */
    public synchronized Holder<O> acquire(@Nullable String pTag) {
        Holder<O> holder = null;
        if (TextUtils.isEmpty(pTag)) {
            for (Map.Entry<String, Holder<O>> entry : mEmptyTagHolderMap.entrySet()) {
                if (entry.getValue() == null || entry.getValue().isAvailable()) {
                    holder = mEmptyTagHolderMap.remove(entry.getKey());
                    break;
                }
            }
            if (holder == null) {
                try {
                    holder = mHolderCreator.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                holder.setTag(String.valueOf(System.currentTimeMillis()));
                mEmptyTagHolderMap.put(holder.getTag(), holder);
            }

        } else {
            holder = mTagHolderMap.get(pTag);
            if (holder == null) {
                try {
                    holder = mHolderCreator.newInstance();
                    mTagHolderMap.put(pTag, holder);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                holder.release();
            }
            holder.setTag(pTag);
        }

        return holder;
    }

    /**
     * 获取一个空闲的 Holder , 若没有, 则创建各新的；
     *
     * @return 可用的holder
     */
    @Override
    public synchronized Holder<O> acquire() {
        return acquire(null);
    }


    /**
     * 释放指定的holder
     *
     * @param pHolder 需要释放的holder
     * @return  返回是否释放成功
     */
    @Override
    public synchronized boolean release(Holder pHolder) {
        pHolder.release();
        return mLockedTagList.remove(pHolder.getTag());
    }

    /**
     * 释放指定tag的holderr
     *
     * @param pTag 需要释放holder的tag
     * @return  返回是否释放成功
     */
    public synchronized boolean release(String pTag) {
        Holder holder = mTagHolderMap.get(pTag);
        return holder != null && release(holder);
    }

    /**
     * 释放所有
     */
    public synchronized void release() {
        for (Map.Entry<String, Holder<O>> holderEntry : mEmptyTagHolderMap.entrySet()) {
            holderEntry.getValue().release();
        }


        for (Map.Entry<String, Holder<O>> holderEntry : mTagHolderMap.entrySet()) {
            holderEntry.getValue().release();
        }

        mEmptyTagHolderMap.clear();
        mTagHolderMap.clear();
    }


    /**
     * 对象的持有类，统一管理对象池内 对 对象的持有及相关调用
     * @param <T> 对象类型
     */
    public static abstract class Holder<T> {
        public final static String TAG = Holder.class.getSimpleName();

        private T mObj;
        private String mTag = "";

        public Holder() {
        }

        public final void warp(T obj) {
            if (this.mObj != null) {
                release(mObj);
            }
            this.mObj = obj;
        }


        final void setTag(String pTag) {
            mTag = pTag;
        }

        final String getTag() {
            return mTag;
        }

        final void release() {
            if (mObj != null) {
                release(mObj);
                mObj = null;
            }
            mTag = "";
        }

        final boolean isAvailable() {
            return mObj == null || isAvailable(mObj);
        }

        /**
         * holder在释放的时候，会调用此方法进行对象的释放操作，
         *
         * @param obj holder持有的对象
         */
        public abstract void release(T obj);

        /**
         * holder在判斷可用性時，调用此方法进行判断
         * 对象是否可用的判断在此方法中实现
         *
         * @param obj holder持有的对象
         * @return 对象是否可用
         */
        public abstract boolean isAvailable(T obj);

        @Override
        public String toString() {
            return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode()) + " - " + "<" + mTag + ">";
        }
    }

}

package pers.nelon.toolkit.player;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import pers.nelon.toolkit.player.controller.IController;
import pers.nelon.toolkit.player.core.DefaultPlayer;
import pers.nelon.toolkit.player.core.IPlayer;
import pers.nelon.toolkit.player.mode.IPlayMode;
import pers.nelon.toolkit.player.mode.Order;
import pers.nelon.toolkit.player.renderer.IRenderer;


/**
 * Created by nelon on 17-7-3.
 */

public class PlayerWrapper<P extends IPlayerItem> implements IPlayer.OnPrepareListener,
        IPlayer.OnCompletionListener,
        IPlayerWrapper<P>, Runnable, IPlayer.OnInfoListener, IPlayer.OnBufferingUpdateListener, IPlayer.OnSeekCompleteListener {

    private static final String TAG = "PlayerWrapper";

    private final IRenderer mRender;
    private final ScheduledExecutorService mScheduledExecutorService;
    private final IController mController;
    private final ExecutorService mSingleThreadExecutor;
    private List<P> mList = new ArrayList<>();
    private int mLastIndex = IPlayMode.NONE;
    private int mCurrentIndex = 0;
    private int mLastPercent = 0;
    private AtomicInteger mCurrentState = new AtomicInteger(State.IDLE);
    private Handler mHandler = new Handler();
    private IPlayer mPlayer;
    private IPlayMode mPlayMode;
    private List<OnProgressUpdateListener> mOnProgressUpdateListenerList = new ArrayList<>();
    private List<PlayStatusListener<P>> mPlayStatusListenerList = new ArrayList<>();
    private List<OnSeekCompleteListener> mSeekCompleteListenerList = new ArrayList<>();
    private List<OnBufferingUpdateListener> mBufferingUpdateListenerList = new ArrayList<>();
    private String mVideoMetaRotation;

    private PlayerWrapper(Builder<P> pBuilder) {
        mPlayer = pBuilder.mPlayer;
        mPlayMode = pBuilder.mPlayMode;
        mList.addAll(pBuilder.mList);
        mCurrentIndex = pBuilder.mIndex;
        mRender = pBuilder.mRender;
        mController = pBuilder.mController;

        if (mPlayer != null) {
            mPlayer.setOnPrepareListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnInfoListener(this);
            mPlayer.setOnBufferingUpdateListener(this);
            mPlayer.setOnSeekCompleteListener(this);
            if (mController != null) {
                mController.attach(this);
            }
            if (mRender != null) {
                mRender.attach(this);
            }
        }

        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);

        mCurrentIndex = 0;
        setStateTo(State.IDLE);
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public static <T extends IPlayerItem> Builder<T> builder() {
        return new Builder<>();
    }

    private void setStateTo(@State.PlayerState int pState) {
        int last = mCurrentState.get();
        if (pState != last) {
            mCurrentState.set(pState);
            notifyPlayStatusListener(PlayStatusListener.STATE_CHANGED, this, last, pState);
        }
    }

    private void notifyPlayStatusListener(final int pWhat, final IPlayerWrapper pWrapper, final Object... pOldAndNew) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (pWhat) {
                    case PlayStatusListener.STATE_CHANGED:
                        int lastState = (int) pOldAndNew[0];
                        int newState = (int) pOldAndNew[1];
                        for (PlayStatusListener playStatusListener : mPlayStatusListenerList) {
                            playStatusListener.onStateChanged(pWrapper, lastState, newState);
                        }
                        break;
                    case PlayStatusListener.ITEM_CHANGED:
                        P oldItem = (P) pOldAndNew[0];
                        P newItem = (P) pOldAndNew[1];
                        for (PlayStatusListener<P> playStatusListener : mPlayStatusListenerList) {
                            playStatusListener.onPlayItemChanged(pWrapper, oldItem, newItem);
                        }
                        break;
                }
            }
        });
    }

    private void notifyOnProgressUpdatedListener(final IPlayerWrapper pWrapper, final int currentPosition, final int pDuration) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (OnProgressUpdateListener onProgressUpdateListener : mOnProgressUpdateListenerList) {
                    onProgressUpdateListener.onProgressUpdate(pWrapper, currentPosition, pDuration);
                }
            }
        });
    }

    private void notifyOnSeekCompletedListener(final IPlayerWrapper pWrapper) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (OnSeekCompleteListener onSeekCompleteListener : mSeekCompleteListenerList) {
                    onSeekCompleteListener.onSeekCompleted(pWrapper);
                }
            }
        });
    }

    private void notifyOnBufferingUpdateListener(final IPlayerWrapper pWrapper, final int pPercent) {
        if (mLastPercent != pPercent) {
            mLastPercent = pPercent;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (OnBufferingUpdateListener onBufferingUpdateListener : mBufferingUpdateListenerList) {
                        onBufferingUpdateListener.onBufferingUpdate(pWrapper, pPercent);
                    }
                }
            });
        }
    }

    @Override
    public void reset() {
        mPlayer.reset();
        setStateTo(State.IDLE);
    }

    @Override
    public void setup(List<P> pList, int pInitialIndex) {
        mList.clear();
        mList.addAll(pList);
        mCurrentIndex = pInitialIndex;
    }

    @Override
    public void start() {
        if (mList.isEmpty()) {
            return;
        }

        switch (getState()) {
            case State.PLAYBACKCOMPLETED:
                mPlayer.seekTo(0);
                mPlayer.start();
                setStateTo(State.STARTED);
                break;
            case State.IDLE:
                if (!mList.isEmpty()) {
                    mPlayer.setDataSource(mList.get(mCurrentIndex).getPath());
                    setStateTo(State.INITIALIZED);
                }
            case State.STOPPED:
                mPlayer.prepareAsync();
                setStateTo(State.PREPARING);

                IPlayerItem old = mLastIndex == IPlayMode.NONE ? null : mList.get(mLastIndex);
                IPlayerItem current = mList.get(mCurrentIndex);

                if ((current == null && old != null) ||
                        current != null && !current.equals(old)) {
                    notifyPlayStatusListener(PlayStatusListener.ITEM_CHANGED, this, old, current);
                }

                break;
            case State.PREPARED:
            case State.PAUSED:
                mPlayer.start();
                setStateTo(State.STARTED);
                break;
            case State.END:
            case State.ERROR:
            case State.INITIALIZED:
            case State.PREPARING:
            case State.STARTED:
            case State.BUFFERING_END:
            case State.BUFFERING_START:
            default:
        }
    }

    @Override
    public void stop() {
        mPlayer.stop();
        setStateTo(State.STOPPED);
    }

    @Override
    public void seekTo(float pPercentage) {
        mPlayer.seekTo((int) (mPlayer.getDuration() * pPercentage + 0.5f));
    }

    @Override
    public int getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public void pause() {
        mPlayer.pause();
        setStateTo(State.PAUSED);
    }

    @Override
    public void onPrepared(IPlayer pPlayer) {
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mVideoMetaRotation = String.valueOf(0);
                if (mRender != null) {
                    try {
                        Log.d(TAG, "mVideoMetaRotation: " + mVideoMetaRotation);
                    } catch (Throwable pE) {
                        pE.printStackTrace();
                    }
                }
                setStateTo(State.PREPARED);
                notifyOnProgressUpdatedListener(PlayerWrapper.this, mPlayer.getCurrentPosition(), mPlayer.getDuration());
                mPlayer.start();
                setStateTo(State.STARTED);
            }
        });
    }

    @Override
    public void onCompletion(IPlayer pPlayer) {
        notifyOnProgressUpdatedListener(this, mPlayer.getDuration(), mPlayer.getDuration());
        setStateTo(State.PLAYBACKCOMPLETED);
        int next = mPlayMode.next(0, mList.size() - 1, mCurrentIndex);

        if (next != IPlayMode.NONE) {
            mLastIndex = mCurrentIndex;
            mCurrentIndex = next;
            reset();
            start();
        }
    }

    @Override
    public boolean onInfo(IPlayer pPlayer, int pWhat, int pExtra) {
        switch (pWhat) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                notifyPlayStatusListener(PlayStatusListener.STATE_CHANGED, this, -1, State.BUFFERING_START);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                notifyPlayStatusListener(PlayStatusListener.STATE_CHANGED, this, -1, State.BUFFERING_END);
                break;
        }
        return false;
    }

    @Override
    public void onBufferingUpdate(IPlayer pPlayer, int percent) {
        notifyOnBufferingUpdateListener(this, percent);
    }

    @Override
    public void onSeekCompleted(IPlayer pPlayer) {
        notifyOnSeekCompletedListener(this);
    }

    public IController getController() {
        return mController;
    }

    public void release() {
        if (mPlayer != null) {
            mPlayer.release();
        }
        if (mRender != null) {
            mRender.release();
        }
        if (mController != null) {
            mController.release();
        }
        mHandler.removeCallbacksAndMessages(null);
        mScheduledExecutorService.shutdown();
        mSingleThreadExecutor.shutdown();
        setStateTo(State.END);

        try {
            Field mSurfaceContext = SurfaceView.class.getDeclaredField("mSurfaceContext");
            Field mMediaPlayerContext = MediaPlayer.class.getDeclaredField("mMediaPlayerContext");
            mSurfaceContext.setAccessible(true);
            mMediaPlayerContext.setAccessible(true);
            mSurfaceContext.set(null, null);
            mMediaPlayerContext.set(null, null);
        } catch (Exception pE) {
            pE.printStackTrace();
        }
    }

    @Override
    public void setPlayMode(IPlayMode pMode) {
        mPlayMode = pMode;
    }

    @Override
    public void playPrev() {
        mCurrentIndex = mPlayMode.togglePrev(0, mList.size() - 1, mCurrentIndex);
        reset();
        start();
    }

    @Override
    public void playNext() {
        mCurrentIndex = mPlayMode.toggleNext(0, mList.size() - 1, mCurrentIndex);
        reset();
        start();
    }

    @Override
    public int getCurrentListIndex() {
        return mCurrentIndex;
    }

    @Override
    public int getAudioSessionId() {
        return mPlayer.getAudioSessionId();
    }

    @Override
    public void addOnProgressUpdateListener(OnProgressUpdateListener pListener) {
        mOnProgressUpdateListenerList.add(pListener);
    }

    @Override
    public void addPlayStatusListener(PlayStatusListener<P> pListener) {
        mPlayStatusListenerList.add(pListener);
    }

    @Override
    public void addOnSeekCompleteListener(OnSeekCompleteListener pOnSeekCompleteListener) {
        mSeekCompleteListenerList.add(pOnSeekCompleteListener);
    }

    @Override
    public void addOnBufferingUpdateListener(OnBufferingUpdateListener pListener) {
        mBufferingUpdateListenerList.add(pListener);
    }

    @Override
    public void removeOnProgressUpdateListener(OnProgressUpdateListener pOnProgressUpdateListener) {
        mOnProgressUpdateListenerList.remove(pOnProgressUpdateListener);
    }

    @Override
    public void removePlayStatusListener(PlayStatusListener pPlayStatusListener) {
        mPlayStatusListenerList.remove(pPlayStatusListener);
    }

    @Override
    public void removeOnSeekCompleteListener(OnSeekCompleteListener pOnSeekCompleteListener) {
        mSeekCompleteListenerList.remove(pOnSeekCompleteListener);
    }

    @Override
    public void removeOnBufferingUpdateListener(OnBufferingUpdateListener pListener) {
        mBufferingUpdateListenerList.remove(pListener);
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public int getState() {
        return mCurrentState.get();
    }

    @Override
    public void run() {
        if (mPlayer.isPlaying()) {
            int currentPosition = mPlayer.getCurrentPosition();
            int duration = mPlayer.getDuration();
            notifyOnProgressUpdatedListener(this, currentPosition, duration);
        }
    }

    @Override
    public void setDisplay(SurfaceHolder pDisplay) {
        mPlayer.setDisplay(pDisplay);
    }

    @Override
    public int getVideoMetaWidth() {
        return mPlayer.getVideoWidth();
    }

    @Override
    public int getVideoMetaHeight() {
        return mPlayer.getVideoHeight();
    }

    @Override
    public int getVideoMetaRotation() {
        return Integer.valueOf(mVideoMetaRotation);
    }

    public static class Builder<PlayerItem extends IPlayerItem> {
        private IPlayer mPlayer;
        private IPlayMode mPlayMode;
        private List<PlayerItem> mList;
        private int mIndex;
        private IRenderer mRender;
        private IController mController;

        private Builder() {
            this.core(new DefaultPlayer())
                    .mode(new Order())
                    .list(new ArrayList<PlayerItem>())
                    .initialIndex(0);
        }

        public Builder<PlayerItem> core(IPlayer pPlayer) {
            mPlayer = pPlayer;
            return this;
        }

        public Builder<PlayerItem> mode(IPlayMode pPlayMode) {
            mPlayMode = pPlayMode;
            return this;
        }

        public Builder<PlayerItem> list(List<PlayerItem> pList) {
            if (mList == null) {
                mList = new ArrayList<>();
            } else {
                mList.clear();
            }

            mList.addAll(pList);
            return this;
        }

        public Builder<PlayerItem> initialIndex(int pIndex) {
            mIndex = pIndex;
            return this;
        }

        public Builder<PlayerItem> renderer(IRenderer pRender) {
            mRender = pRender;
            return this;
        }

        public Builder<PlayerItem> controller(IController pController) {
            mController = pController;
            return this;
        }

        public IPlayerWrapper<PlayerItem> build() {
            return new PlayerWrapper<>(this);
        }

    }
}

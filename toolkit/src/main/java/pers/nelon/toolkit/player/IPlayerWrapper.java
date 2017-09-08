package pers.nelon.toolkit.player;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import pers.nelon.toolkit.player.controller.IController;
import pers.nelon.toolkit.player.mode.IPlayMode;

/**
 * Created by nelon on 17-7-4.
 */

public interface IPlayerWrapper<PlayerItem extends IPlayerItem> {
    void addOnProgressUpdateListener(OnProgressUpdateListener pListener);

    void addPlayStatusListener(PlayStatusListener<PlayerItem> pListener);

    void addOnSeekCompleteListener(OnSeekCompleteListener pListener);

    void addOnBufferingUpdateListener(OnBufferingUpdateListener pListener);

    void removeOnProgressUpdateListener(OnProgressUpdateListener pOnProgressUpdateListener);

    void removePlayStatusListener(PlayStatusListener<PlayerItem> pPlayStatusListener);

    void removeOnSeekCompleteListener(OnSeekCompleteListener pDefaultController);

    void removeOnBufferingUpdateListener(OnBufferingUpdateListener pListener);

    boolean isPlaying();

    @State.PlayerState
    int getState();

    void pause();

    void start();

    void stop();

    void seekTo(float pPercentage);

    int getDuration();

    void setDisplay(SurfaceHolder pHolder);

    int getVideoMetaWidth();

    int getVideoMetaHeight();

    int getVideoMetaRotation();

    void reset();

    void setup(List<PlayerItem> pList, int pInitialIndex);

    IController getController();

    void release();

    void setPlayMode(IPlayMode pMode);

    void playPrev();

    void playNext();

    int getCurrentListIndex();

    int getAudioSessionId();

    interface OnProgressUpdateListener {
        void onProgressUpdate(IPlayerWrapper pWrapper, long pCurrent, long pDuration);
    }

    interface PlayStatusListener<T> {
        int STATE_CHANGED = 0;
        int ITEM_CHANGED = 1;

        void onPlayItemChanged(@NonNull IPlayerWrapper pWrapper, @Nullable T pOld, @Nullable T pNew);

        void onStateChanged(@NonNull IPlayerWrapper pWrapper, @State.PlayerState int pOld, @State.PlayerState int pNew);

    }

    interface OnSeekCompleteListener {
        void onSeekCompleted(IPlayerWrapper pWrapper);
    }


    interface OnBufferingUpdateListener {
        void onBufferingUpdate(IPlayerWrapper pWrapper, int pPercent);
    }

    interface State {
        int IDLE = 1;
        int INITIALIZED = 2;
        int PREPARED = 3;
        int STARTED = 4;
        int STOPPED = 5;
        int PREPARING = 6;
        int PAUSED = 7;
        int PLAYBACKCOMPLETED = 8;
        int END = 9;
        int ERROR = 10;
        int BUFFERING_START = 11;
        int BUFFERING_END = 12;

        @IntDef({IDLE, INITIALIZED, PREPARED, STARTED, STOPPED, PREPARING, PAUSED, PLAYBACKCOMPLETED, END, ERROR, BUFFERING_START, BUFFERING_END})
        @Retention(RetentionPolicy.SOURCE)
        @interface PlayerState {
        }
    }
}

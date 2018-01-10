package pers.nelon.toolkit.player.core;

import android.view.SurfaceHolder;

/**
 * Created by nelon on 17-7-3.
 */

public interface IPlayer {
    void reset();

    void start();

    void prepareAsync();

    void setDataSource(String pPath);

    void setDisplay(SurfaceHolder pSurfaceHolder);

    void release();

    void setOnPrepareListener(OnPrepareListener pListener);

    void setOnCompletionListener(OnCompletionListener pListener);

    void setOnInfoListener(OnInfoListener pListener);

    void setOnErrorListener(OnErrorListener pListener);

    void setOnBufferingUpdateListener(OnBufferingUpdateListener pListener);

    void setOnSeekCompleteListener(OnSeekCompleteListener pListener);

    boolean isPlaying();

    int getCurrentPosition();

    int getDuration();

    void pause();

    void stop();

    void seekTo(int pL);

    int getVideoWidth();

    int getVideoHeight();

    int getAudioSessionId();

    interface OnPrepareListener {
        void onPrepared(IPlayer pPlayer);
    }

    interface OnCompletionListener {
        void onCompletion(IPlayer pPlayer);
    }

    interface OnInfoListener {
        boolean onInfo(IPlayer pPlayer, int pWhat, int pExtra);
    }

    interface OnErrorListener {
        boolean onError(IPlayer pPlayer, int pWhat, int pExtra);
    }

    interface OnBufferingUpdateListener {
        void onBufferingUpdate(IPlayer pPlayer, int percent);
    }

    interface OnSeekCompleteListener {
        void onSeekCompleted(IPlayer pPlayer);
    }


}


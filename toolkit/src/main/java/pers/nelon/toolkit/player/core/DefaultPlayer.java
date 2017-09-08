package pers.nelon.toolkit.player.core;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;

/**
 * Created by nelon on 17-7-3.
 */

public class DefaultPlayer implements IPlayer {
    private static final String TAG = "DefaultPlayer";
    private final MediaPlayer mMediaPlayer;

    public DefaultPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setScreenOnWhilePlaying(true);
        mMediaPlayer.setAuxEffectSendLevel(1);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(false);
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
    }

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void prepareAsync() {
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void setOnPrepareListener(final OnPrepareListener pListener) {
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pListener.onPrepared(DefaultPlayer.this);
            }
        });
    }

    @Override
    public void setOnCompletionListener(final OnCompletionListener pListener) {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pListener.onCompletion(DefaultPlayer.this);
            }
        });
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void setOnInfoListener(final OnInfoListener pListener) {
        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                return pListener.onInfo(DefaultPlayer.this, what, extra);
            }
        });
    }

    @Override
    public void setOnBufferingUpdateListener(final OnBufferingUpdateListener pListener) {
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                pListener.onBufferingUpdate(DefaultPlayer.this, percent);
            }
        });
    }

    @Override
    public void setOnSeekCompleteListener(final OnSeekCompleteListener pListener) {
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                pListener.onSeekCompleted(DefaultPlayer.this);
            }
        });
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public void seekTo(int pL) {
        mMediaPlayer.seekTo(pL);
    }

    @Override
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    @Override
    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    @Override
    public void setDataSource(String pPath) {
        try {
            mMediaPlayer.setDataSource(pPath);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (Exception pE) {
            pE.printStackTrace();
        }
    }

    @Override
    public void setDisplay(SurfaceHolder pSurfaceHolder) {
        mMediaPlayer.setDisplay(pSurfaceHolder);
    }

    @Override
    public void release() {
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.release();
    }
}

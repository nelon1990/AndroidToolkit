package pers.nelon.toolkit.player.mode;

/**
 * Created by nelon on 17-7-10.
 */

public interface IModeFactory<T> {
    IPlayMode create(T pIn);
}

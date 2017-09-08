package pers.nelon.toolkit.player.mode;

/**
 * Created by nelon on 17-7-3.
 */

public interface IPlayMode {
    int NONE = -1;

    int toggleNext(int pStart, int pEnd, int pCurrentIndex);

    int togglePrev(int pStart, int pEnd, int pCurrentIndex);

    int next(int pStart, int pEnd, int pCurrentIndex);
}

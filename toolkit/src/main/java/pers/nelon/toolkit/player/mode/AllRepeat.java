package pers.nelon.toolkit.player.mode;

/**
 * Created by nelon on 17-7-3.
 */

public class AllRepeat implements IPlayMode {
    @Override
    public int toggleNext(int pStart, int pEnd, int pCurrentIndex) {
        return next(pStart, pEnd, pCurrentIndex);
    }

    @Override
    public int togglePrev(int pStart, int pEnd, int pCurrentIndex) {
        int next = pCurrentIndex - 1;
        if (next < 0) {
            next = pEnd;
        }
        return next;
    }

    @Override
    public int next(int pStart, int pEnd, int pCurrentIndex) {
        int next = pCurrentIndex + 1;
        if (next > pEnd) {
            next = pStart;
        }
        return next;
    }
}

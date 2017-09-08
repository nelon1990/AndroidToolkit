package pers.nelon.toolkit.player.mode;

/**
 * Created by nelon on 17-7-3.
 */

public class Order implements IPlayMode {
    private IPlayMode mAllRepeat = new AllRepeat();

    @Override
    public int toggleNext(int pStart, int pEnd, int pCurrentIndex) {
        return mAllRepeat.toggleNext(pStart, pEnd, pCurrentIndex);
    }

    @Override
    public int togglePrev(int pStart, int pEnd, int pCurrentIndex) {
        return mAllRepeat.togglePrev(pStart, pEnd, pCurrentIndex);
    }

    @Override
    public int next(int pStart, int pEnd, int pCurrentIndex) {
        int next = pCurrentIndex + 1;
        if (next > pEnd) {
            next = NONE;
        }
        return next;
    }

}

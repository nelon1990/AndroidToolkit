package pers.nelon.toolkit.player.mode;

/**
 * Created by nelon on 17-7-3.
 */

public class Once implements IPlayMode {
    private IPlayMode mIPlayMode = new AllRepeat();

    @Override
    public int toggleNext(int pStart, int pEnd, int pCurrentIndex) {
        return mIPlayMode.toggleNext(pStart, pEnd, pCurrentIndex);
    }

    @Override
    public int togglePrev(int pStart, int pEnd, int pCurrentIndex) {
        return mIPlayMode.togglePrev(pStart, pEnd, pCurrentIndex);
    }

    @Override
    public int next(int pStart, int pEnd, int pCurrentIndex) {
        return NONE;
    }
}

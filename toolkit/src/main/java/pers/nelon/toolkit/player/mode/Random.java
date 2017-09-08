package pers.nelon.toolkit.player.mode;

/**
 * Created by nelon on 17-7-10.
 */

public class Random implements IPlayMode {

    private final java.util.Random mR;

    public Random() {
        mR = new java.util.Random();
    }


    @Override
    public int toggleNext(int pStart, int pEnd, int pCurrentIndex) {
        return next(pStart, pEnd, pCurrentIndex);
    }

    @Override
    public int togglePrev(int pStart, int pEnd, int pCurrentIndex) {
        return next(pStart, pEnd, pCurrentIndex);
    }

    @Override
    public int next(int pStart, int pEnd, int pCurrentIndex) {
        int i = mR.nextInt(pEnd - pStart + 1) + pStart;
        if (i == pCurrentIndex) {
            return next(pStart, pEnd, pCurrentIndex);
        }
        return i;
    }

}

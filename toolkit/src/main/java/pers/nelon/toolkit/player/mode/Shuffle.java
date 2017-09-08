package pers.nelon.toolkit.player.mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Created by nelon on 17-7-10.
 */

public class Shuffle implements IPlayMode {
    private final Random mRandom;
    private Stack<Integer> mHistory = new Stack<>();

    public Shuffle() {
        mRandom = new Random();
    }

    @Override
    public int toggleNext(int pStart, int pEnd, int pCurrentIndex) {
        return next(pStart, pEnd, pCurrentIndex);
    }

    @Override
    public int togglePrev(int pStart, int pEnd, int pCurrentIndex) {
        int result;
        if (mHistory.isEmpty()) {
            result = next(pStart, pEnd, pCurrentIndex);
        } else {
            result = mHistory.pop();
        }

        return result;
    }

    @Override
    public int next(int pStart, int pEnd, int pCurrentIndex) {
        mHistory.push(pCurrentIndex);

        if (pEnd == pStart) {
            return pCurrentIndex;
        }

        List<Integer> list = new ArrayList<>();
        for (int i = pStart; i <= pEnd; i++) {
            if (pCurrentIndex != i && !mHistory.contains(i)) {
                list.add(i);
            }
        }

        if (list.isEmpty()) {
            mHistory.clear();
            return next(pStart, pEnd, pCurrentIndex);
        } else {
            return mRandom.nextInt(list.size());
        }
    }

}

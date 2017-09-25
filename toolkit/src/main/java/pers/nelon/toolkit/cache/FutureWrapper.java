package pers.nelon.toolkit.cache;

/**
 * Created by nelon on 17-9-25.
 */

class FutureWrapper implements ICommitFuture {
    private ICommitFuture[] mCommitFutures;

    FutureWrapper(ICommitFuture... pFutures) {
        mCommitFutures = pFutures;
    }

    @Override
    public boolean committing() {
        boolean result = true;
        for (ICommitFuture commitFuture : mCommitFutures) {
            result &= commitFuture.committing();
        }
        return result;
    }

    @Override
    public void abortCommit() {
        for (ICommitFuture commitFuture : mCommitFutures) {
            commitFuture.abortCommit();
        }
    }
}

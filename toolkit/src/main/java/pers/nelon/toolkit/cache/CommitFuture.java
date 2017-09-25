package pers.nelon.toolkit.cache;

import java.util.concurrent.Future;

/**
 * Created by nelon on 17-9-25.
 */

class CommitFuture implements ICommitFuture {

    private final Future<?>[] mSubmit;

    CommitFuture(Future<?>... pFutures) {
        mSubmit = pFutures;
    }

    @Override
    public boolean committing() {
        boolean result = true;
        for (Future<?> future : mSubmit) {
            result &= !future.isDone();
        }
        return result;
    }

    @Override
    public void abortCommit() {
        for (Future<?> future : mSubmit) {
            if (!future.isCancelled()) {
                future.cancel(true);
            }
        }
    }
}

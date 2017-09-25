package pers.nelon.toolkit.cache;

/**
 * Created by nelon on 17-9-25.
 */

public interface ICommitFuture {

    boolean committing();

    void abortCommit();

}

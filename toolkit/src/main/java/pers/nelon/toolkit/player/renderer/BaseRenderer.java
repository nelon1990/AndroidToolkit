package pers.nelon.toolkit.player.renderer;

/**
 * Created by nelon on 17-7-3.
 */

public abstract class BaseRenderer implements IRenderer {

    @Override
    public final void release() {
        releaseInner();
    }

    protected abstract void releaseInner();
}

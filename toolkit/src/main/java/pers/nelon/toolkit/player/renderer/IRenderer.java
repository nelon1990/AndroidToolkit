package pers.nelon.toolkit.player.renderer;


import pers.nelon.toolkit.player.IPlayerWrapper;

/**
 * Created by nelon on 17-7-3.
 */

public interface IRenderer {
    void attach(IPlayerWrapper pPlayer);

    void release();
}

package pers.nelon.toolkit.player.controller;


import pers.nelon.toolkit.player.IPlayerWrapper;

/**
 * Created by nelon on 17-7-4.
 */

public interface IController {
    boolean handle(int pKeyCode, int pAction);

    void attach(IPlayerWrapper pWrapper);

    void release();
}

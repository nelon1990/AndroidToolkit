package pers.nelon.toolkit.cache.impl;

import java.io.OutputStream;

/**
 * Created by nelon on 17-9-8.
 */

public interface ICacheWriter<T> {
    boolean write(OutputStream pOutputStream);

    long getLength();
}

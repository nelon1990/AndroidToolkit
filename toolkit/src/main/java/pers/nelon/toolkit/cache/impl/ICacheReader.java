package pers.nelon.toolkit.cache.impl;

import java.io.InputStream;

/**
 * Created by nelon on 17-9-8.
 */

public interface ICacheReader<T> {
    T read(InputStream pInputStream);
}

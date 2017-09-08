package pers.nelon.toolkit.cache.impl.reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.io.InputStream;

import pers.nelon.toolkit.cache.impl.ICacheReader;

/**
 * Created by nelon on 17-9-8.
 */

public class BitmapReader implements ICacheReader<Bitmap> {

    private final BitmapFactory.Options mOptions;
    private final Rect mRect;

    public BitmapReader(Rect pRect, BitmapFactory.Options pOptions) {
        mRect = pRect;
        mOptions = pOptions;
    }

    @Override
    public Bitmap read(InputStream pInputStream) {
        return BitmapFactory.decodeStream(pInputStream, mRect, mOptions);
    }
}

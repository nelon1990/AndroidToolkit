package pers.nelon.toolkit.cache.impl.writer;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.OutputStream;

import pers.nelon.toolkit.cache.impl.ICacheWriter;

/**
 * Created by nelon on 17-9-8.
 */

public class BitmapWriter implements ICacheWriter<Bitmap> {

    private final Bitmap.CompressFormat mFormat;
    private final int mQuality;
    private final Bitmap mBitmap;

    public BitmapWriter(Bitmap pBitmap, Bitmap.CompressFormat pFormat, int pQuality) {
        mFormat = pFormat;
        mQuality = pQuality;
        mBitmap = pBitmap;
    }

    @Override
    public boolean write(OutputStream pOutputStream) {
        boolean result;
        try {
            result = mBitmap.compress(mFormat, mQuality, pOutputStream);
            if (result) {
                pOutputStream.flush();
            }
        } catch (IOException pE) {
            pE.printStackTrace();
            result = false;
        }
        return result;
    }

    @Override
    public long getLength() {
        return mBitmap.getRowBytes() * mBitmap.getHeight();
    }
}

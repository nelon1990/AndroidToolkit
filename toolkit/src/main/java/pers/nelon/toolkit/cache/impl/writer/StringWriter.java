package pers.nelon.toolkit.cache.impl.writer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import pers.nelon.toolkit.cache.impl.ICacheWriter;

/**
 * Created by 李冰锋 on 2017/9/24.
 * E-mail:libf@ppfuns.com
 * pers.nelon.toolkit.cache.impl.writer
 */

public class StringWriter implements ICacheWriter<String> {

    private final String mStr;

    public StringWriter(String pStr) {
        mStr = pStr;
    }

    @Override
    public boolean write(OutputStream pOutputStream) {
        boolean result = false;

        DataOutputStream dataOutputStream = new DataOutputStream(pOutputStream);
        try {
            dataOutputStream.writeUTF(mStr);
            result = true;
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        return result;
    }

    @Override
    public long getLength() {
        return mStr.length();
    }
}

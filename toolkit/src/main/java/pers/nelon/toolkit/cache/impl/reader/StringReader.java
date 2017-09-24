package pers.nelon.toolkit.cache.impl.reader;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import pers.nelon.toolkit.cache.impl.ICacheReader;

/**
 * Created by 李冰锋 on 2017/9/24.
 * E-mail:libf@ppfuns.com
 * pers.nelon.toolkit.cache.impl.reader
 */

public class StringReader implements ICacheReader<String> {
    @Override
    public String read(InputStream pInputStream) {
        String result = null;
        DataInputStream dataInputStream = new DataInputStream(pInputStream);
        try {
            result = dataInputStream.readUTF();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        return result;
    }
}

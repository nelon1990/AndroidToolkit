package pers.nelon.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import pers.nelon.toolkit.TaggedPool;
import pers.nelon.toolkit.cache.Cache;
import pers.nelon.toolkit.cache.impl.reader.BitmapReader;
import pers.nelon.toolkit.cache.impl.reader.StringReader;
import pers.nelon.toolkit.cache.impl.writer.StringWriter;
import pers.nelon.toolkit.utils.L;


public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.tv_title);

        Cache.init(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BitmapDrawable drawable = (BitmapDrawable) getDrawable(R.mipmap.ic_launcher_round);
            Cache.withL2Cache()
                    .editor()
                    .put("ic_launcher_round", drawable.getBitmap())
                    .commitAsync();
            Bitmap bitmap = Cache.withL2Cache()
                    .get("ic_launcher_round", new BitmapReader(null, null), (Bitmap) null);
            ImageView imageView = (ImageView) findViewById(R.id.iv_image);
            imageView.setImageBitmap(bitmap);

            Cache.withL2Cache()
                    .editor()
                    .put("key", new StringWriter("ic_launcher_round"))
                    .commit();
            String key = Cache.withL2Cache()
                    .get("key", new StringReader(), "NULL");
            mTextView.setText(key);
        }
        L.v("A", "A", "A", "A", "A");

        L.v(1, 2, "21", 'a', new ArrayMap<>(), 1.3f);
        L.d(1, 2, "21", 'a', new ArrayMap<>(), 1.3f);
        L.i(1, 2, "21", 'a', new ArrayMap<>(), 1.3f);
        L.w(1, 2, "21", 'a', new ArrayMap<>(), 1.3f);
        L.e(1, 2, "21", 'a', new ArrayMap<>(), 1.3f);
        L.wtf(1, 2, "21", 'a', new ArrayMap<>(), 1.3f);
    }

    class ActivityHolder extends TaggedPool.Holder<Activity> {
        @Override
        public void release(Activity obj) {
            obj.finish();
        }

        @Override
        public boolean isAvailable(Activity obj) {
            return obj.isFinishing();
        }
    }


}


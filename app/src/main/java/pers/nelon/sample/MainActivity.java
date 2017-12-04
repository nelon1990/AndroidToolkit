package pers.nelon.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import pers.nelon.toolkit.TaggedPool;
import pers.nelon.toolkit.cache.Cache;
import pers.nelon.toolkit.cache.impl.reader.BitmapReader;
import pers.nelon.toolkit.cache.impl.reader.StringReader;
import pers.nelon.toolkit.cache.impl.writer.BitmapWriter;
import pers.nelon.toolkit.cache.impl.writer.StringWriter;


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
                    .put("ic_launcher_round", new BitmapWriter(drawable.getBitmap(), Bitmap.CompressFormat.PNG, 100))
                    .commit();
            Bitmap bitmap = Cache.withL2Cache()
                    .get("ic_launcher_round", new BitmapReader(null, null), (Bitmap) null);
            ImageView imageView = (ImageView) findViewById(R.id.iv_image);
            imageView.setImageBitmap(bitmap);

            Cache.withL2Cache()
                    .editor()
                    .put("key", new StringWriter("ic_launcher_round"))
                    .commit();
            String key = Cache.withL2Cache()
                    .get("key", new StringReader(),"NULL");
            mTextView.setText(key);
        }

        TaggedPool<Activity> pool = TaggedPool.newInstance(ActivityHolder.class);

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


package pers.nelon.sample;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import pers.nelon.toolkit.cache.Cache;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Cache.init(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BitmapDrawable drawable = (BitmapDrawable) getDrawable(R.mipmap.ic_launcher_round);
            Cache.withDiskDefault().editor()
                    .put("ic_launcher_round", drawable.getBitmap())
                    .commit();
            Bitmap bitmap = null;
            bitmap = Cache.withDiskDefault().get("ic_launcher_round", bitmap);

            ImageView imageView = (ImageView) findViewById(R.id.iv_image);
            imageView.setImageBitmap(bitmap);
        }
    }

}


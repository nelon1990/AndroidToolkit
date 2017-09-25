package pers.nelon.toolkit.cache;


import android.annotation.SuppressLint;
import android.app.Fragment;

/**
 * Created by nelon on 17-9-25.
 */

@SuppressLint("ValidFragment")
class LifeCircleFragment extends Fragment {
    private static final String TAG = "LifeCircleFragment";

    @Override
    public void onPause() {
        super.onPause();
        Cache.flush();
    }

    @Override
    public void onStop() {
        super.onStop();
        Cache.flush();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Cache.release();
    }
}

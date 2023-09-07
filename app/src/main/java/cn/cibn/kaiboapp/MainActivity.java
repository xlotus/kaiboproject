package cn.cibn.kaiboapp;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;

import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tv.lib.core.Logger;
import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.change.ChangedListener;
import com.tv.lib.core.lang.ObjectStore;
import com.tv.lib.frame.activity.BaseActivity;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.data.CoverManager;
import cn.cibn.kaibo.data.VideoHistoryManager;
import cn.cibn.kaibo.ui.MainFragment;
import cn.cibn.kaibo.utils.ToastUtils;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends BaseActivity implements ChangedListener {
    private static final String TAG = "MainActivity";
    private static final long TOAST_DURATION = 3000L;
    private MainFragment mainFragment;

    private MyNetworkCallBack myNetworkCallBack;
    private NetworkReceiver networkReceiver;


    private TextView tvToast;
    private float toastTranslationY;
    private ObjectAnimator toastShowAnimator, toastHideAnimator;

    private TXCloudVideoView cloudVideoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvToast = findViewById(R.id.tv_toast);
        initAnimator();

        String intentLiveId = "";
        Uri data = getIntent().getData();
        if (data != null) {
            intentLiveId = data.getQueryParameter("id");
            Logger.d(TAG, "data = " + data + ", id = " + intentLiveId);
//            DataManger.getInstance().setDefaultLiveId(intentLiveId);
        }
//        DataManger.getInstance().setDefaultLiveId("1007842");
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (savedInstanceState == null) {
            mainFragment = MainFragment.createInstance(intentLiveId);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.live_main_fragment, mainFragment);
            transaction.commitNow();
        }

        findViewById(R.id.btn_left).setOnClickListener(v -> {
            simulateKey(KeyEvent.KEYCODE_DPAD_LEFT);
        });
        findViewById(R.id.btn_right).setOnClickListener(v -> {
            simulateKey(KeyEvent.KEYCODE_DPAD_RIGHT);
        });
        findViewById(R.id.btn_up).setOnClickListener(v -> {
            simulateKey(KeyEvent.KEYCODE_DPAD_UP);
        });
        findViewById(R.id.btn_down).setOnClickListener(v -> {
            simulateKey(KeyEvent.KEYCODE_DPAD_DOWN);
        });
        ConfigModel.getInstance().init();

        registerNetworkListener();

        cloudVideoView = findViewById(R.id.cloud_video_view);
        CoverManager.getInstance().init(this, cloudVideoView);
        VideoHistoryManager.getInstance().init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unRegisterNetworkListener();
        ChangeListenerManager.getInstance().unregisterChangedListener(ChangedKeys.CHANGED_SHOW_TOAST, this);
        CoverManager.getInstance().destroy();
        super.onDestroy();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mainFragment != null) {
            boolean ret = mainFragment.onKeyDown(keyCode, event);
            if (ret) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onListenerChange(String key, Object value) {
        if (ChangedKeys.CHANGED_SHOW_TOAST.equals(key)) {
            if (value instanceof String && tvToast != null) {
                tvToast.setText((String) value);
                toastShowAnimator.cancel();
                toastShowAnimator.start();
                tvToast.removeCallbacks(hideToastTask);
                tvToast.postDelayed(hideToastTask, TOAST_DURATION);
            }
        }
    }

    private void initAnimator() {
        toastTranslationY = getResources().getDimension(R.dimen.dp_140);
        tvToast.setTranslationY(toastTranslationY);

        toastShowAnimator = ObjectAnimator.ofFloat(tvToast, "translationY", toastTranslationY, 0);
        toastShowAnimator.setDuration(500);
        toastShowAnimator.setInterpolator(new OvershootInterpolator());
        toastHideAnimator = ObjectAnimator.ofFloat(tvToast, "translationY", 0, toastTranslationY);
        toastHideAnimator.setDuration(500);
    }

    private void simulateKey(int keyCode) {
        if (true) {
            onKeyDown(keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
            return;
        }
        Runtime runtime = Runtime.getRuntime();
        try {
//            runtime.exec("input keyevent " + keyCode);
            new Thread() {
                @Override
                public void run() {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                }
            }.start();

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private void registerNetworkListener() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager != null) {
            myNetworkCallBack = new MyNetworkCallBack();
            connectivityManager.registerDefaultNetworkCallback(myNetworkCallBack);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && connectivityManager != null) {
            myNetworkCallBack = new MyNetworkCallBack();
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), myNetworkCallBack);
        }
        else {
            networkReceiver = new NetworkReceiver();
            registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    private void unRegisterNetworkListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (myNetworkCallBack != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                connectivityManager.unregisterNetworkCallback(myNetworkCallBack);
            }
        } else {
            if (networkReceiver != null) {
                unregisterReceiver(networkReceiver);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static class MyNetworkCallBack extends ConnectivityManager.NetworkCallback {
        long startTime = System.currentTimeMillis();
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Logger.d(TAG, "onAvailable | " + network);
            if (System.currentTimeMillis() - startTime < 1000) {
                Logger.d(TAG, "onAvailable ignore ");
                return;
            }
            ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_NETWORK, true);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Logger.d(TAG, "onLost | " + network);
            if (System.currentTimeMillis() - startTime < 1000) {
                return;
            }
            ToastUtils.showToast(ObjectStore.getContext().getString(R.string.no_network));
            ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_NETWORK, false);
        }
    }

    private static class NetworkReceiver extends BroadcastReceiver {
        boolean first = true;
        @Override
        public void onReceive(Context context, Intent intent) {
            if (first) {
                first = false;
                return;
            }
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                Logger.d(TAG, "NetworkReceiver noConnectivity = " + noConnectivity);
                if (noConnectivity) {
                    ToastUtils.showToast(ObjectStore.getContext().getString(R.string.no_network));
                }
                ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_NETWORK, !noConnectivity);
            }
        }
    }

    private Runnable hideToastTask = new Runnable() {
        @Override
        public void run() {
            toastHideAnimator.start();
        }
    };
}
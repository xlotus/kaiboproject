package cn.cibn.kaiboapp;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.ui.MainFragment;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {

    private MainFragment mainFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mainFragment = MainFragment.createInstance("");
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
}
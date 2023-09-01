package cn.cibn.kaibo.ui.settings;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;

import com.tv.lib.core.lang.thread.TaskHelper;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.databinding.FragmentCheckNetworkBinding;
import cn.cibn.kaibo.ui.KbBaseFragment;

public class CheckNetworkFragment extends KbBaseFragment<FragmentCheckNetworkBinding> implements View.OnClickListener, Handler.Callback {
    private static final int WHAT_PROGRESS = 1;

    private Handler handler;
    private int progress;

    public static CheckNetworkFragment createInstance() {
        return new CheckNetworkFragment();
    }

    @Override
    protected FragmentCheckNetworkBinding createBinding(LayoutInflater inflater) {
        return FragmentCheckNetworkBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        handler = new Handler(this);
        binding.btnCheckNow.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updateView() {

    }

    @Override
    public void requestFocus() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.btnCheckNow.getId()) {
            checkWifi();
        }
    }


    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_PROGRESS) {
            progress += 1;
            if (progress > 100) {
                progress = 0;
            }
            binding.progressTv2wifi.setProgress(progress);
            handler.sendEmptyMessageDelayed(WHAT_PROGRESS, 10);
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void checkWifi() {
        binding.btnCheckNow.setVisibility(View.GONE);
        binding.viewBgCheckNetwork.setFocusable(true);
        binding.viewBgCheckNetwork.requestFocus();

        binding.ivWifiStatus.setVisibility(View.VISIBLE);
        binding.ivWifiStatus.setImageResource(R.drawable.settings_net_checking);
        binding.tvWifiStatus.setVisibility(View.VISIBLE);
        binding.tvWifiStatus.setText("正在检查……");
        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setRepeatCount(Animation.INFINITE);
        binding.ivWifiStatus.startAnimation(animation);
        handler.sendEmptyMessageDelayed(WHAT_PROGRESS, 10);

        TaskHelper.exec(new TaskHelper.Task() {
            boolean netOk = false;
            @Override
            public void execute() throws Exception {
                Thread.sleep(2000);
                netOk = true;
            }

            @Override
            public void callback(Exception e) {
                handler.removeCallbacksAndMessages(null);
                binding.ivWifiStatus.clearAnimation();
                if (netOk) {
                    binding.progressTv2wifi.setProgress(100);
                    binding.ivWifiStatus.setImageResource(R.drawable.settings_net_ok);
                    binding.tvWifiStatus.setText("WIFI连接正常");
                } else {
                    binding.ivWifiStatus.setImageResource(R.drawable.settings_net_error);
                    binding.tvWifiStatus.setText("WIFI连接异常");
                    binding.progressTv2wifi.setProgress(0);
                    binding.progressTv2wifi.setSecondaryProgress(100);
                }
            }
        });
    }
}

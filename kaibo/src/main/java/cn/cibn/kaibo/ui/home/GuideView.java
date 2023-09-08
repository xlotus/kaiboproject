package cn.cibn.kaibo.ui.home;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.change.ChangedListener;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.data.ConfigModel;
import cn.cibn.kaibo.databinding.LayoutGuideBinding;
import cn.cibn.kaibo.imageloader.ImageLoadHelper;
import cn.cibn.kaibo.model.ModelConfig;

public class GuideView extends RelativeLayout implements Handler.Callback, ChangedListener {
    private static final int DURATION = 500;
    private static final int DEFAULT_SHOW_INTERVAL = 10000;
    private static final int WHAT_LEFT_SHOW = 1;
    private static final int WHAT_TOP_SHOW = 2;
    private static final int WHAT_RIGHT_SHOW = 3;
    private static final int WHAT_BOTTOM_SHOW = 4;
    private static final int WHAT_LEFT_HIDE = 5;
    private static final int WHAT_TOP_HIDE = 6;
    private static final int WHAT_RIGHT_HIDE = 7;
    private static final int WHAT_BOTTOM_HIDE = 8;

    private Handler handler;
    private LayoutGuideBinding binding;

    private ObjectAnimator leftShowAnimator, leftHideAnimator;
    private ObjectAnimator topShowAnimator, topHideAnimator;
    private ObjectAnimator rightShowAnimator, rightHideAnimator;
    private ObjectAnimator bottomShowAnimator, bottomHideAnimator;

    private int leftShowInterval = DEFAULT_SHOW_INTERVAL;
    private int topShowInterval = DEFAULT_SHOW_INTERVAL;
    private int rightShowInterval = DEFAULT_SHOW_INTERVAL;
    private int bottomShowInterval = DEFAULT_SHOW_INTERVAL;

    private boolean opened = false;

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        handler = new Handler(this);
        binding = LayoutGuideBinding.inflate(LayoutInflater.from(context), this);
        initAnimator();
        updateConfig();
        if (opened) {
            showLeft();
            showTop();
            showRight();
            showBottom();
        }
    }

    public void open() {
        opened = true;
        if (binding == null) {
            return;
        }
        showLeft();
        showTop();
        showRight();
        showBottom();
    }

    private void initAnimator() {
        float translationDistance = getResources().getDimension(R.dimen.dp_300);
        binding.ivGuideLeft.setTranslationX(-translationDistance);
        binding.ivGuideTop.setTranslationY(-translationDistance);
        binding.ivGuideRight.setTranslationX(translationDistance);
        binding.ivGuideBottoom.setTranslationY(translationDistance);

        leftShowAnimator = ObjectAnimator.ofFloat(binding.ivGuideLeft, "translationX", -translationDistance, 0);
        leftShowAnimator.setDuration(DURATION);
        leftHideAnimator = ObjectAnimator.ofFloat(binding.ivGuideLeft, "translationX", 0, -translationDistance);
        leftHideAnimator.setDuration(DURATION);

        rightShowAnimator = ObjectAnimator.ofFloat(binding.ivGuideRight, "translationX", translationDistance, 0);
        rightShowAnimator.setDuration(DURATION);
        rightHideAnimator = ObjectAnimator.ofFloat(binding.ivGuideRight, "translationX", 0, translationDistance);
        rightHideAnimator.setDuration(DURATION);

        topShowAnimator = ObjectAnimator.ofFloat(binding.ivGuideTop, "translationY", -translationDistance, 0);
        topShowAnimator.setDuration(DURATION);
        topHideAnimator = ObjectAnimator.ofFloat(binding.ivGuideTop, "translationY", 0, -translationDistance);
        topHideAnimator.setDuration(DURATION);

        bottomShowAnimator = ObjectAnimator.ofFloat(binding.ivGuideBottoom, "translationY", translationDistance, 0);
        bottomShowAnimator.setDuration(DURATION);
        bottomHideAnimator = ObjectAnimator.ofFloat(binding.ivGuideBottoom, "translationY", 0, translationDistance);
        bottomHideAnimator.setDuration(DURATION);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        int what = msg.what;
        if (what == WHAT_LEFT_SHOW) {
            showLeft();
            return true;
        }
        if (what == WHAT_LEFT_HIDE) {
            hideLeft();
            return true;
        }
        if (what == WHAT_TOP_SHOW) {
            showTop();
            return true;
        }
        if (what == WHAT_TOP_HIDE) {
            hideTop();
            return true;
        }
        if (what == WHAT_RIGHT_SHOW) {
            showRight();
            return true;
        }
        if (what == WHAT_RIGHT_HIDE) {
            hideRight();
            return true;
        }
        if (what == WHAT_BOTTOM_SHOW) {
            showBottom();
            return true;
        }
        if (what == WHAT_BOTTOM_HIDE) {
            hideBottom();
            return true;
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ChangeListenerManager.getInstance().registerChangedListener(ChangedKeys.CHANGED_GRAY_MODE, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ChangeListenerManager.getInstance().unregisterChangedListener(ChangedKeys.CHANGED_GRAY_MODE, this);
    }

    @Override
    public void onListenerChange(String key, Object value) {
        if (ChangedKeys.CHANGED_GRAY_MODE.equals(key)) {
            updateConfig();
        }
    }

    private void showLeft() {
        leftShowAnimator.start();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(WHAT_LEFT_HIDE, leftShowInterval);
        }
    }

    private void hideLeft() {
        leftHideAnimator.start();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(WHAT_LEFT_SHOW, leftShowInterval);
        }
    }

    private void showTop() {
        topShowAnimator.start();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(WHAT_TOP_HIDE, topShowInterval);
        }
    }

    private void hideTop() {
        topHideAnimator.start();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(WHAT_TOP_SHOW, topShowInterval);
        }
    }

    private void showRight() {
        rightShowAnimator.start();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(WHAT_RIGHT_HIDE, rightShowInterval);
        }
    }

    private void hideRight() {
        rightHideAnimator.start();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(WHAT_RIGHT_SHOW, rightShowInterval);
        }
    }

    private void showBottom() {
        bottomShowAnimator.start();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(WHAT_BOTTOM_HIDE, bottomShowInterval);
        }
    }

    private void hideBottom() {
        bottomHideAnimator.start();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(WHAT_BOTTOM_SHOW, bottomShowInterval);
        }
    }

    private void updateConfig() {
        ModelConfig config = ConfigModel.getInstance().getModelConfig();
        if (config == null) {
            return;
        }
        if (config.getIs_open() <= 0) {
            return;
        }
        boolean isGrayMode = ConfigModel.getInstance().isGrayMode();
        if (config.getLeft_guide() > 0 && !TextUtils.isEmpty(config.getLeft_guide_url())) {
            leftShowInterval = config.getLeft_guide();
            ImageLoadHelper.loadImage(binding.ivGuideLeft, config.getLeft_guide_url(), isGrayMode, R.drawable.guide_left);
        }
        if (config.getTop_guide() > 0 && !TextUtils.isEmpty(config.getTop_guide_url())) {
            topShowInterval = config.getTop_guide();
            ImageLoadHelper.loadImage(binding.ivGuideTop, config.getTop_guide_url(), isGrayMode, R.drawable.guide_up);
        }
        if (config.getRight_guide() > 0 && !TextUtils.isEmpty(config.getRight_guide_url())) {
            rightShowInterval = config.getRight_guide();
            ImageLoadHelper.loadImage(binding.ivGuideRight, config.getRight_guide_url(), isGrayMode, R.drawable.guide_right);
        }
        if (config.getUnder_guide() > 0 && !TextUtils.isEmpty(config.getUnder_guide_url())) {
            bottomShowInterval = config.getUnder_guide();
            ImageLoadHelper.loadImage(binding.ivGuideBottoom, config.getUnder_guide_url(), isGrayMode, R.drawable.guide_down);
        }
    }
}

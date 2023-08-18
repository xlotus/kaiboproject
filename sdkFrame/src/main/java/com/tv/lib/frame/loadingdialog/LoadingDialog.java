package com.tv.lib.frame.loadingdialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.tv.lib.R;
import com.tv.lib.core.Logger;

/**
 *
 */
public class LoadingDialog extends Dialog {

    public interface OnDialogDismissedListener {
        void onDialogDismissed();
    }

    private static final int loading = 0;
    public static final int success = 1;
    public static final int error = 2;
    private ProgressBar loadingProgressbar;
    private ImageView loadingPicSuccess;
    private TextView loadingTextView;
    private int loading_result;
    private String loading_text;
    private static final String DEFAULT_LOADING_TEXT = "加载中...";
    private Context mContext;
    private OnDialogDismissedListener onDialogDismissed;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.myTransparent);
        setContentView(R.layout.dialog_laoding);
        mContext = context;
        initView();
    }

    private LoadingDialog setLoading_result(int loading_result) {
        this.loading_result = loading_result;
        return this;
    }

    private LoadingDialog setLoading_text(String loading_text) {
        this.loading_text = loading_text;
        return this;
    }

    void showLoadingDialog(String text) {
        if (this.isShowing()) {
            Logger.d("LoadingDialog", "can not show dialog cause dialog is showing");
            return;
        }

        setLoading_result(loading);
        setLoading_text(text);
        this.show();

    }

    void hideSuccess(String loading_text) {
        setLoading_result(success);
        setLoading_text(loading_text);
        hideDialog();
    }

    void hideError(String loading_text) {
        setLoading_result(error);
        setLoading_text(loading_text);
        hideDialog();
    }

    void hideNow() {
        dismiss();
    }


    private void hideDialog() {
        hideProgress();
        loadText();
        loadImageAndDismiss();
    }

    public void setOnDialogDismissed(OnDialogDismissedListener onDialogDismissed) {
        this.onDialogDismissed = onDialogDismissed;
    }

    void refreshLoadingText(String text) {
        if (!this.isShowing()) {
            Logger.d("LoadingDialog", "can not change test cause dialog is not showing");
            return;
        }
        loadingTextView.setText(text);
    }

    @Override
    public void show() {
        super.show();

        hideImage();
        showProgress();
        loadText();
    }

    private void showProgress() {
        loadingProgressbar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        loadingProgressbar.setVisibility(View.GONE);
    }

    private void loadImageAndDismiss() {
        showImage();
        switch (loading_result) {
            case 1:
                Glide.with(mContext).load(R.drawable.icon_right_green).
                        diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .transition(DrawableTransitionOptions.withCrossFade(450))
                        .into(loadingPicSuccess);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            dismiss();
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case 2:
                Glide.with(mContext).load(R.drawable.icon_error_red)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .transition(DrawableTransitionOptions.withCrossFade(450))
                        .into(loadingPicSuccess);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            dismiss();
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.onDialogDismissed != null) {
            this.onDialogDismissed.onDialogDismissed();
        }
    }

    private void loadText() {

        switch (loading_result) {
            case 0:
                if (loading_text == null || "".equals(loading_text)) {
                    loading_text = DEFAULT_LOADING_TEXT;
                }
                loadingTextView.setTextAppearance(mContext, R.style.LoadingTextStyle);
                loadingTextView.setTextColor(mContext.getResources().getColor(R.color.white_b));
                break;
            case 1:
                loadingTextView.setTextAppearance(mContext, R.style.ErrorTextStyle);
                loadingTextView.setTextColor(mContext.getResources().getColor(R.color.green_c));
                break;
            case 2:
                loadingTextView.setTextAppearance(mContext, R.style.ErrorTextStyle);
                loadingTextView.setTextColor(mContext.getResources().getColor(R.color.red_a));
                break;
        }
        loadingTextView.setText(loading_text);
    }

    private void hideImage() {
        loadingPicSuccess.setVisibility(View.GONE);
    }

    private void showImage() {
        loadingPicSuccess.setVisibility(View.VISIBLE);
    }

    private void initView() {
        loadingProgressbar = findViewById(R.id.loading_progressbar);
        loadingPicSuccess = findViewById(R.id.loading_pic_success);
        loadingTextView = (TextView) findViewById(R.id.loading_textView);
    }

}

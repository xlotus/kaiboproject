package cn.cibn.kaibo.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;
import com.tv.lib.core.Logger;
import com.tv.lib.core.change.ChangeListenerManager;
import com.tv.lib.core.lang.thread.TaskHelper;

import cn.cibn.kaibo.R;
import cn.cibn.kaibo.UserManager;
import cn.cibn.kaibo.change.ChangedKeys;
import cn.cibn.kaibo.databinding.LayoutLoginBinding;
import cn.cibn.kaibo.model.ModelLogin;
import cn.cibn.kaibo.model.ModelWrapper;
import cn.cibn.kaibo.network.LiveMethod;
import cn.cibn.kaibo.utils.LoginHelper;

public class LoginView extends ConstraintLayout {
    private static final String TAG = "LoginView";
    private LayoutLoginBinding loginBinding;

    public LoginView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        loginBinding = LayoutLoginBinding.inflate(LayoutInflater.from(context), this);
        setBackgroundResource(R.color.color_161823_90);
    }

    public void login() {
        loginBinding.ivLoginQrcode.requestFocus();
        loginBinding.progressLoadQrcode.setVisibility(VISIBLE);
        loginBinding.tvHasScanned.setVisibility(GONE);
        LoginHelper.login(new OAuthListener() {
            @Override
            public void onAuthGotQrcode(String s, byte[] bytes) {
                Logger.d(TAG, "onAuthGotQrcode = " + s);
                if (bytes != null) {
                    Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    loginBinding.ivLoginQrcode.setImageBitmap(b);
                    loginBinding.progressLoadQrcode.setVisibility(GONE);
                }
            }

            @Override
            public void onQrcodeScanned() {
                Logger.d(TAG, "onQrcodeScanned");
                loginBinding.tvHasScanned.setVisibility(VISIBLE);
            }

            @Override
            public void onAuthFinish(OAuthErrCode oAuthErrCode, String code) {
                Logger.d(TAG, "onAuthFinish code = " + code);
                loginBinding.tvHasScanned.setText("登录中");
                loginServer(code);
            }
        });
    }

    private void loginServer(String code) {
        TaskHelper.exec(new TaskHelper.Task() {
            ModelWrapper<ModelLogin> model;

            @Override
            public void execute() throws Exception {
                model = LiveMethod.getInstance().reqLogin(code);
            }

            @Override
            public void callback(Exception e) {
                if (model != null && model.isSuccess()) {
                    UserManager.getInstance().setToken(model.getData().getAccess_token());
                    UserManager.getInstance().setUserInfo(model.getData());
                    ChangeListenerManager.getInstance().notifyChange(ChangedKeys.CHANGED_LOGIN_SUCCESS);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LoginHelper.stopAuth();
    }
}
